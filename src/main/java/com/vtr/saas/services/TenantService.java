package com.vtr.saas.services;

import com.vtr.saas.common.PageResponse;
import com.vtr.saas.responses.RegisterTenantRequest;
import com.vtr.saas.responses.TenantResponse;

public interface TenantService {

    void registerTenant(final RegisterTenantRequest request);

    void approveTenant(final String tenantId);

    void activateTenant(final String tenantId);

    void deactivateTenant(final String tenantId);

    void suspendTenant(final String tenantId);

    PageResponse<TenantResponse> findAll(final int page, final int size);
}
