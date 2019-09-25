package org.openmbee.sdvc.core.security;

import org.aopalliance.intercept.MethodInvocation;
import org.openmbee.sdvc.core.services.PermissionService;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionOperations;
import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.authentication.AuthenticationTrustResolverImpl;
import org.springframework.security.core.Authentication;

public class CustomMSEHandler extends DefaultMethodSecurityExpressionHandler {

    private ObjectFactory<PermissionService> permissionService;

    public CustomMSEHandler(ObjectFactory<PermissionService> permissionService) {
        super();
        this.permissionService = permissionService;
    }

    @Override
    protected MethodSecurityExpressionOperations createSecurityExpressionRoot(
        Authentication authentication, MethodInvocation invocation) {
        CustomMSERoot root =
            new CustomMSERoot(authentication, permissionService.getObject());
        root.setThis(invocation.getThis());
        root.setPermissionEvaluator(getPermissionEvaluator());
        root.setTrustResolver(getTrustResolver());
        root.setRoleHierarchy(getRoleHierarchy());
        root.setDefaultRolePrefix(getDefaultRolePrefix());
        return root;
    }
}
