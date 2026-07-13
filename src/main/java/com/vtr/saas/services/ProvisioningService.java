package com.vtr.saas.services;

import com.vtr.saas.entities.Tenant;


public interface ProvisioningService {

    void provisionTenant(final Tenant tenant);
}
