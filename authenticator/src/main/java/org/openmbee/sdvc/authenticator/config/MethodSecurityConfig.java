package org.openmbee.sdvc.authenticator.config;

import org.openmbee.sdvc.core.security.CustomMSEHandler;
import org.openmbee.sdvc.core.services.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class MethodSecurityConfig extends GlobalMethodSecurityConfiguration {

    @Autowired
    public PermissionService permissionService;

    @Override
    protected MethodSecurityExpressionHandler createExpressionHandler() {
        return new CustomMSEHandler(permissionService);
    }
}