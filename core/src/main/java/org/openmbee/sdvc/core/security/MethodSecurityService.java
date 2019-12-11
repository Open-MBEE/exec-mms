package org.openmbee.sdvc.core.security;

import java.util.HashSet;
import java.util.Set;
import org.openmbee.sdvc.core.services.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
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
        Set<String> groups = new HashSet<>();
        if (isAdmin(authentication, groups)) {
            return true;
        }
        if (permissionService.hasOrgPrivilege(privilege, authentication.getName(), groups, orgId)) {
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
        Set<String> groups = new HashSet<>();
        if (isAdmin(authentication, groups)) {
            return true;
        }
        if (permissionService.hasProjectPrivilege(privilege, authentication.getName(), groups, projectId)) {
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
        Set<String> groups = new HashSet<>();
        if (isAdmin(authentication, groups)) {
            return true;
        }
        if (permissionService.hasBranchPrivilege(privilege, authentication.getName(), groups, projectId, branchId)) {
            return true;
        }
        return false;
    }

    public static boolean isAdmin(Authentication auth, Set<String> groups) {
        boolean isAdmin = false;
        for (GrantedAuthority ga: auth.getAuthorities()) {
            if ("mmsadmin".equals(ga.getAuthority())) {
                isAdmin = true;
            }
            groups.add(ga.getAuthority());
        }
        return isAdmin;
    }
}
