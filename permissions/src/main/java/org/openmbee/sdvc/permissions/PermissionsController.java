package org.openmbee.sdvc.permissions;

import javax.transaction.Transactional;
import org.openmbee.sdvc.core.config.Privileges;
import org.openmbee.sdvc.core.services.PermissionService;
import org.openmbee.sdvc.permissions.exceptions.PermissionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PermissionsController {

    PermissionService permissionService;

    @Autowired
    public PermissionsController(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    @PostMapping(value = "/orgs/{orgId}/permissions")
    @Transactional
    public ResponseEntity<?> updateOrgPermissions(
        @PathVariable String orgId,
        @RequestBody PermissionsRequest req,
        Authentication auth) {

        checkUpdatePerm(auth, orgId, null, null);
        if (req.getGroups() != null) {
            permissionService.updateOrgGroupPerms(req.getGroups(), orgId);
        }
        if (req.getUsers() != null) {
            permissionService.updateOrgUserPerms(req.getUsers(), orgId);
        }
        if (req.getPublic() != null) {
            permissionService.setOrgPublic(req.getPublic(), orgId);
        }
        return ResponseEntity.ok("");
    }

    @PostMapping(value = "/projects/{projectId}/permissions")
    @Transactional
    public ResponseEntity<?> updateProjectPermissions(
        @PathVariable String projectId,
        @RequestBody PermissionsRequest req,
        Authentication auth) {

        checkUpdatePerm(auth, null, projectId, null);
        if (req.getGroups() != null) {
            permissionService.updateProjectGroupPerms(req.getGroups(), projectId);
        }
        if (req.getUsers() != null) {
            permissionService.updateProjectUserPerms(req.getUsers(), projectId);
        }
        if (req.getPublic() != null) {
            permissionService.setProjectPublic(req.getPublic(), projectId);
        }
        if (req.getInherit() != null) {
            permissionService.setProjectInherit(req.getInherit(), projectId);
        }
        return ResponseEntity.ok("");
    }

    @PostMapping(value = "/projects/{projectId}/refs/{refId}/permissions")
    @Transactional
    public ResponseEntity<?> updateBranchPermissions(
        @PathVariable String projectId,
        @PathVariable String refId,
        @RequestBody PermissionsRequest req,
        Authentication auth) {

        checkUpdatePerm(auth, null, projectId, refId);
        if (req.getGroups() != null) {
            permissionService.updateBranchGroupPerms(req.getGroups(), projectId, refId);
        }
        if (req.getUsers() != null) {
            permissionService.updateBranchUserPerms(req.getUsers(), projectId, refId);
        }
        if (req.getInherit() != null) {
            permissionService.setBranchInherit(req.getInherit(), projectId, refId);
        }
        return ResponseEntity.ok("");
    }

    @GetMapping(value = "/orgs/{orgId}/permissions")
    public PermissionsResponse getOrgPermissions(
        @PathVariable String orgId,
        Authentication auth) {

        checkReadPerm(auth, orgId, null, null);
        PermissionsResponse res = new PermissionsResponse();
        res.setGroups(permissionService.getOrgGroupRoles(orgId));
        res.setUsers(permissionService.getOrgUserRoles(orgId));
        res.setPublic(permissionService.isOrgPublic(orgId));
        return res;
    }

    @GetMapping(value = "/projects/{projectId}/permissions")
    public PermissionsResponse getProjectPermissions(
        @PathVariable String projectId,
        Authentication auth) {

        checkReadPerm(auth, null, projectId, null);
        PermissionsResponse res = new PermissionsResponse();
        res.setGroups(permissionService.getProjectGroupRoles(projectId));
        res.setUsers(permissionService.getProjectUserRoles(projectId));
        res.setPublic(permissionService.isProjectPublic(projectId));
        res.setInherit(permissionService.isProjectInherit(projectId));
        return res;
    }

    @GetMapping(value = "/projects/{projectId}/refs/{refId}/permissions")
    public PermissionsResponse getBranchPermissions(
        @PathVariable String projectId,
        @PathVariable String refId,
        Authentication auth) {

        checkReadPerm(auth, null, projectId, refId);
        PermissionsResponse res = new PermissionsResponse();
        res.setGroups(permissionService.getBranchGroupRoles(projectId, refId));
        res.setUsers(permissionService.getBranchUserRoles(projectId, refId));
        res.setPublic(permissionService.isProjectPublic(projectId));
        res.setInherit(permissionService.isBranchInherit(projectId, refId));
        return res;
    }

    private void checkUpdatePerm(Authentication auth, String orgId, String projectId, String refId) {
        if (auth instanceof AnonymousAuthenticationToken) {
            throw new PermissionException(HttpStatus.UNAUTHORIZED, "");
        }
        if (orgId != null && permissionService.hasOrgPrivilege(Privileges.ORG_UPDATE_PERMISSIONS.name(), auth.getName(), orgId)) {
            return;
        }
        if (projectId != null) {
            if (refId != null && permissionService.hasBranchPrivilege(Privileges.BRANCH_UPDATE_PERMISSIONS.name(), auth.getName(), projectId, refId)) {
                    return;
            }
            if (permissionService.hasProjectPrivilege(Privileges.PROJECT_UPDATE_PERMISSIONS.name(), auth.getName(), projectId)) {
                return;
            }
        }
        throw new PermissionException(HttpStatus.FORBIDDEN, "");
    }

    private void checkReadPerm(Authentication auth, String orgId, String projectId, String refId) {
        if (orgId != null && (permissionService.isOrgPublic(orgId) || permissionService.hasOrgPrivilege(Privileges.ORG_READ_PERMISSIONS.name(), auth.getName(), orgId))) {
            return;
        }
        if (projectId != null) {
            if (permissionService.isProjectPublic(projectId)) {
                return;
            }
            if (refId != null && permissionService.hasBranchPrivilege(Privileges.BRANCH_READ_PERMISSIONS.name(), auth.getName(), projectId, refId)) {
                return;
            }
            if (permissionService.hasProjectPrivilege(Privileges.PROJECT_READ_PERMISSIONS.name(), auth.getName(), projectId)) {
                return;
            }
        }
        throw new PermissionException(HttpStatus.FORBIDDEN, "");
    }
}
