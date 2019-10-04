package org.openmbee.sdvc.core.security;

import java.util.HashSet;
import java.util.Set;
import org.openmbee.sdvc.core.services.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service("mss")
public class MethodSecurityService {

    private PermissionService permissionService;

    @Autowired
    public void setPermissionService(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    public boolean hasOrgPrivilege(Authentication authentication, String orgId, String privilege, boolean allowAnonIfPublic) {
        if (allowAnonIfPublic && permissionService.isOrgPublic(orgId)) {
            return true;
        }
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            return false;
        }
        if (permissionService.hasOrgPrivilege(privilege, authentication.getName(), getGroups(authentication), orgId)) {
            return true;
        }
        return false;
    }

    public boolean hasProjectPrivilege(Authentication authentication, String projectId, String privilege, boolean allowAnonIfPublic) {
        if (allowAnonIfPublic && permissionService.isProjectPublic(projectId)) {
            return true;
        }
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            return false;
        }
        if (permissionService.hasProjectPrivilege(privilege, authentication.getName(), getGroups(authentication), projectId)) {
            return true;
        }
        return false;
    }

    public boolean hasBranchPrivilege(Authentication authentication, String projectId, String branchId, String privilege, boolean allowAnonIfPublic) {
        if (allowAnonIfPublic && permissionService.isProjectPublic(projectId)) {
            return true;
        }
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            return false;
        }
        if (permissionService.hasBranchPrivilege(privilege, authentication.getName(), getGroups(authentication), projectId, branchId)) {
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
