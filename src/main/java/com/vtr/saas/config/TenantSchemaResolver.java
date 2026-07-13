package com.vtr.saas.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class TenantSchemaResolver {

    private final JdbcTemplate jdbcTemplate;

    private static final String PUBLIC_SCHEMA = "public";

    @Cacheable(value = "tenantSchemas", key = "#tenantId")
    public String resolveTenantSchema(final String tenantId) {
        if(tenantId == null){
            return PUBLIC_SCHEMA;
        }

        try {
            final String companyCode = this.jdbcTemplate.queryForObject(
                    "SELECT company_code FROM public.tenants WHERE id = ? and deleted = false", String.class, tenantId);
            if(companyCode != null){
                final String schemaName = "tenant_" + companyCode.toLowerCase();
                log.debug("Tenant schema resolved: {} for tenant {}", schemaName, tenantId);
                return schemaName;
            }
            log.warn("Tenant schema not found for tenant {}", tenantId);
            return PUBLIC_SCHEMA;
        } catch (final Exception e){
            log.error("Error resolving tenant schema for tenant {}", tenantId, e);
            return PUBLIC_SCHEMA;
        }
    }
}
