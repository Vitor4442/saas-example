package com.vtr.saas.services.impl;

import com.vtr.saas.common.PageResponse;
import com.vtr.saas.entities.Tenant;
import com.vtr.saas.entities.TenantStatus;
import com.vtr.saas.entities.User;
import com.vtr.saas.entities.UserRole;
import com.vtr.saas.exceptions.DuplicateResourceException;
import com.vtr.saas.exceptions.InvalidRequestException;
import com.vtr.saas.mappers.TenantMapper;
import com.vtr.saas.repositories.TenantRepository;
import com.vtr.saas.repositories.UserRepository;
import com.vtr.saas.responses.RegisterTenantRequest;
import com.vtr.saas.responses.TenantResponse;
import com.vtr.saas.services.ProvisioningService;
import com.vtr.saas.services.TenantService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;

@Setter
@RequiredArgsConstructor
@Slf4j
public class TenantServiceImpl implements TenantService {

    private final TenantRepository tenantRepository;
    private final TenantMapper tenantMapper;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final ProvisioningService provisioningService;

    @Override
    public void registerTenant(RegisterTenantRequest request) {

        if(this.tenantRepository.existsByCompanyCode(request.getCompanyCode())){
            throw new DuplicateResourceException("Tenant Already Existing");
        }

        if(this.tenantRepository.existsByEmail(request.getEmail())){
            throw new DuplicateResourceException("Email Already Existing");
        }

        final Tenant tenant = this.tenantMapper.toEntity(request);
        tenant.setAdminPassword(this.passwordEncoder.encode(request.getAdminPassword()));
        tenant.setStatus(TenantStatus.PENDING);
        this.tenantRepository.save(tenant);
    }

    @Override
    public void approveTenant(final String tenantId) {
        // check if tenant exists
        final Tenant tenant = this.tenantRepository.findById(tenantId)
                .orElseThrow(() -> new EntityNotFoundException("Tenant does not exist"));

        // activate tenant
        tenant.setStatus(TenantStatus.ACTIVE);
        this.tenantRepository.save(tenant);

        try {
            // provision the schema for the tenant
            this.provisioningService.provisionTenant(tenant);
            // create initial admin user
            createInitiaAdminUser(tenant);
        } catch (final Exception e) {
            rollbackTenantStatus(tenant);
        }

    }


    @Override
    public void activateTenant(String tenantId) {
        final Tenant tenant = this.tenantRepository.findById(tenantId).orElseThrow(() -> new EntityNotFoundException("Tenant does not exist"));

        if(tenant.getStatus() != TenantStatus.PENDING){
            throw new InvalidRequestException("Tenant is not in peding status");
        }

        tenant.setStatus(TenantStatus.ACTIVE);
        this.tenantRepository.save(tenant);
    }

    @Override
    public void deactivateTenant(String tenantId) {
        final Tenant tenant = this.tenantRepository.findById(tenantId).orElseThrow(() -> new EntityNotFoundException("Tenant does not exist"));

        if(tenant.getStatus() != TenantStatus.PENDING){
            throw new InvalidRequestException("Tenant is not in peding status");
        }

        tenant.setStatus(TenantStatus.INACTIVE);
        this.tenantRepository.save(tenant);
    }

    @Override
    public void suspendTenant(String tenantId) {
        final Tenant tenant = this.tenantRepository.findById(tenantId).orElseThrow(() -> new EntityNotFoundException("Tenant does not exist"));

        if(tenant.getStatus() != TenantStatus.PENDING){
            throw new InvalidRequestException("Tenant is not in peding status");
        }

        tenant.setStatus(TenantStatus.SUSPENDED);
        this.tenantRepository.save(tenant);
    }

    @Override
    public PageResponse<TenantResponse> findAll(int page, int size) {
        final PageRequest pageRequest = PageRequest.of(page, size);
        final Page<Tenant> tenants = this.tenantRepository.findAll(pageRequest);
        final Page<TenantResponse> tenantResponses = tenants.map(this.tenantMapper::toResponse);
        return PageResponse.of(tenantResponses);
    }


    private void createInitiaAdminUser(Tenant tenant) {
        if (this.userRepository.existsByUsername(tenant.getAdminUsername())){
            throw new DuplicateResourceException("User alreadt exists");
        }

        final User adminUser = User.builder()
                .username(tenant.getAdminUsername())
                .email(tenant.getEmail())
                .firstName(extractFirstName(tenant.getAdminFullname()))
                .lastName(extractLastName(tenant.getAdminFullname()))
                .password(tenant.getAdminPassword())
                .role(UserRole.ROLE_COMPANY_ADMIN)
                .tenant(tenant)
                .deleted(false)
                .build();

        this.userRepository.save(adminUser);
        log.info("Admin user created sucessfully");
    }

    private String extractLastName(String adminFullname) {
        return adminFullname.split(" ").length > 1 ? adminFullname.split(" ")[1] : adminFullname;
    }

    private String extractFirstName(String adminFullname) {
        return adminFullname.split(" ")[0];
    }

    private void rollbackTenantStatus(final Tenant tenant) {
        tenant.setStatus(TenantStatus.PENDING);
        this.tenantRepository.save(tenant);
    }

}
