package org.openmbee.sdvc.crud.config;

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