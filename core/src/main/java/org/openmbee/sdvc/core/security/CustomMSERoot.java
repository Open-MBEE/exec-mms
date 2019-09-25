package org.openmbee.sdvc.core.security;

import java.util.HashSet;
import java.util.Set;
import org.openmbee.sdvc.core.services.PermissionService;
import org.springframework.security.access.expression.SecurityExpressionRoot;
import org.springframework.security.access.expression.method.MethodSecurityExpressionOperations;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;

public class CustomMSERoot extends SecurityExpressionRoot implements
    MethodSecurityExpressionOperations {

    private Object filterObject;
    private Object returnObject;
    private Object target;
    private Set<String> groups;

    public PermissionService permissionService;

    public CustomMSERoot(
        Authentication authentication, PermissionService permissionService) {
        super(authentication);
        this.permissionService = permissionService;
        groups = getGroups(authentication);
    }

    public void setFilterObject(Object filterObject) {
        this.filterObject = filterObject;
    }

    public Object getFilterObject() {
        return filterObject;
    }

    public void setReturnObject(Object returnObject) {
        this.returnObject = returnObject;
    }

    public Object getReturnObject() {
        return returnObject;
    }

    /**
     * Sets the "this" property for use in expressions. Typically this will be the "this"
     * property of the {@code JoinPoint} representing the method invocation which is being
     * protected.
     *
     * @param target the target object on which the method in is being invoked.
     */
    void setThis(Object target) {
        this.target = target;
    }

    public Object getThis() {
        return target;
    }

    public boolean hasOrgPrivilege(String orgId, String privilege, boolean allowAnonIfPublic) {
        if (allowAnonIfPublic && permissionService.isOrgPublic(orgId)) {
            return true;
        }
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            return false;
        }
        if (permissionService.hasOrgPrivilege(privilege, authentication.getName(), groups, orgId)) {
            return true;
        }
        return false;
    }

    public boolean hasProjectPrivilege(String projectId, String privilege, boolean allowAnonIfPublic) {
        if (allowAnonIfPublic && permissionService.isProjectPublic(projectId)) {
            return true;
        }
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            return false;
        }
        if (permissionService.hasProjectPrivilege(privilege, authentication.getName(), groups, projectId)) {
            return true;
        }
        return false;
    }

    public boolean hasBranchPrivilege(String projectId, String branchId, String privilege, boolean allowAnonIfPublic) {
        if (allowAnonIfPublic && permissionService.isProjectPublic(projectId)) {
            return true;
        }
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            return false;
        }
        if (permissionService.hasBranchPrivilege(privilege, authentication.getName(), groups, projectId, branchId)) {
            return true;
        }
        return false;
    }

    public static Set<String> getGroups(Authentication auth) {
        Set<String> res = new HashSet<>();
        auth.getAuthorities().forEach(ga ->res.add(ga.getAuthority()));
        return res;
    }
}
