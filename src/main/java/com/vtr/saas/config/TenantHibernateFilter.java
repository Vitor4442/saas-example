package com.vtr.saas.config;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.hibernate.Session;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class TenantHibernateFilter {

    @PersistenceContext
    private EntityManager entityManager;

    @Before("execution(* com.vtr.saas.services.*.*(..))")
    public void activateFilter() {
        final String tenantId = TenantContext.getCurrentTenant();
        if (tenantId != null){
            final Session session = entityManager.unwrap(Session.class);

            //activate the filter to inject the tenantId
            session.enableFilter("tenantFilter")
                    .setParameter("tenantId", tenantId);
        }
    }
}
