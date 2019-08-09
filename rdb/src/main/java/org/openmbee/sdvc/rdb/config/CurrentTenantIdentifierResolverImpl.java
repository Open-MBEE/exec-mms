package org.openmbee.sdvc.rdb.config;

import org.hibernate.context.spi.CurrentTenantIdentifierResolver;

public class CurrentTenantIdentifierResolverImpl implements CurrentTenantIdentifierResolver {

    @Override
    public String resolveCurrentTenantIdentifier() {
        return DbContextHolder.getContext().getKey();
    }

    @Override
    public boolean validateExistingCurrentSessions() {
        return true;
    }

}