package com.vtr.saas.repositories;

import com.vtr.saas.entities.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TenantRepository extends JpaRepository<Tenant, String> {


    boolean existsByCompanyCode(String companyCode);

    boolean existsByEmail(String email);
}
