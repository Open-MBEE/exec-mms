package org.openmbee.sdvc.permissions;

import javax.transaction.Transactional;

import org.openmbee.sdvc.core.builders.PermissionUpdatesResponseBuilder;
import org.openmbee.sdvc.core.objects.PermissionUpdatesResponse;
import org.openmbee.sdvc.core.services.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
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

    @PostMapping(value = "/orgs/{orgId}/permissions", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    @PreAuthorize("@mss.hasOrgPrivilege(authentication, #orgId, 'ORG_UPDATE_PERMISSIONS', false)")
    public PermissionUpdatesResponse updateOrgPermissions(
        @PathVariable String orgId,
        @RequestBody PermissionsRequest req) {

        PermissionUpdatesResponse response = new PermissionUpdatesResponse();

        if (req.getGroups() != null) {
            response.setGroups(permissionService.updateOrgGroupPerms(req.getGroups(), orgId));
        }
        if (req.getUsers() != null) {
            response.setUsers(permissionService.updateOrgUserPerms(req.getUsers(), orgId));
        }
        if (req.getPublic() != null) {
            response.setPublic(permissionService.setOrgPublic(req.getPublic(), orgId));
        }
        return response;
    }

    @PostMapping(value = "/projects/{projectId}/permissions", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    @PreAuthorize("@mss.hasProjectPrivilege(authentication, #projectId, 'PROJECT_UPDATE_PERMISSIONS', false)")
    public PermissionUpdatesResponse updateProjectPermissions(
        @PathVariable String projectId,
        @RequestBody PermissionsRequest req) {

        PermissionUpdatesResponseBuilder builder = new PermissionUpdatesResponseBuilder();

        if (req.getGroups() != null) {
            builder.addGroups(permissionService.updateProjectGroupPerms(req.getGroups(), projectId));
        }
        if (req.getUsers() != null) {
            builder.addUsers(permissionService.updateProjectUserPerms(req.getUsers(), projectId));
        }
        if (req.getPublic() != null) {
            builder.setPublic(permissionService.setProjectPublic(req.getPublic(), projectId));
        }
        if (req.getInherit() != null) {
            builder.add(permissionService.setProjectInherit(req.getInherit(), projectId));
        }
        return builder.getPermissionUpdatesReponse();
    }

    @PostMapping(value = "/projects/{projectId}/refs/{refId}/permissions", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    @PreAuthorize("@mss.hasBranchPrivilege(authentication, #projectId, #refId, 'BRANCH_UPDATE_PERMISSIONS', false)")
    public PermissionUpdatesResponse updateBranchPermissions(
        @PathVariable String projectId,
        @PathVariable String refId,
        @RequestBody PermissionsRequest req) {

        PermissionUpdatesResponseBuilder builder = new PermissionUpdatesResponseBuilder();

        if (req.getGroups() != null) {
            builder.addGroups(permissionService.updateBranchGroupPerms(req.getGroups(), projectId, refId));
        }
        if (req.getUsers() != null) {
            builder.addUsers(permissionService.updateBranchUserPerms(req.getUsers(), projectId, refId));
        }
        if (req.getInherit() != null) {
            builder.add(permissionService.setBranchInherit(req.getInherit(), projectId, refId));
        }
        return builder.getPermissionUpdatesReponse();
    }

    @GetMapping(value = "/orgs/{orgId}/permissions")
    @PreAuthorize("@mss.hasOrgPrivilege(authentication, #orgId, 'ORG_READ_PERMISSIONS', true)")
    public PermissionsResponse getOrgPermissions(
        @PathVariable String orgId) {

        PermissionsResponse res = new PermissionsResponse();
        res.setGroups(permissionService.getOrgGroupRoles(orgId));
        res.setUsers(permissionService.getOrgUserRoles(orgId));
        res.setPublic(permissionService.isOrgPublic(orgId));
        return res;
    }

    @GetMapping(value = "/projects/{projectId}/permissions")
    @PreAuthorize("@mss.hasProjectPrivilege(authentication, #projectId, 'PROJECT_READ_PERMISSIONS', true)")
    public PermissionsResponse getProjectPermissions(
        @PathVariable String projectId) {

        PermissionsResponse res = new PermissionsResponse();
        res.setGroups(permissionService.getProjectGroupRoles(projectId));
        res.setUsers(permissionService.getProjectUserRoles(projectId));
        res.setPublic(permissionService.isProjectPublic(projectId));
        res.setInherit(permissionService.isProjectInherit(projectId));
        return res;
    }

    @GetMapping(value = "/projects/{projectId}/refs/{refId}/permissions")
    @PreAuthorize("@mss.hasBranchPrivilege(authentication, #projectId, #refId, 'BRANCH_READ_PERMISSIONS', true)")
    public PermissionsResponse getBranchPermissions(
        @PathVariable String projectId,
        @PathVariable String refId) {

        PermissionsResponse res = new PermissionsResponse();
        res.setGroups(permissionService.getBranchGroupRoles(projectId, refId));
        res.setUsers(permissionService.getBranchUserRoles(projectId, refId));
        res.setPublic(permissionService.isProjectPublic(projectId));
        res.setInherit(permissionService.isBranchInherit(projectId, refId));
        return res;
    }
}
