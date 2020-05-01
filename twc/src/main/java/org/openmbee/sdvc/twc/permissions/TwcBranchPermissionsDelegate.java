package org.openmbee.sdvc.twc.permissions;

import exceptions.TwcPermissionException;
import org.openmbee.sdvc.core.builders.PermissionUpdateResponseBuilder;
import org.openmbee.sdvc.core.builders.PermissionUpdatesResponseBuilder;
import org.openmbee.sdvc.core.delegation.PermissionsDelegate;
import org.openmbee.sdvc.core.objects.PermissionResponse;
import org.openmbee.sdvc.core.objects.PermissionUpdateRequest;
import org.openmbee.sdvc.core.objects.PermissionUpdateResponse;
import org.openmbee.sdvc.core.objects.PermissionUpdatesResponse;
import org.openmbee.sdvc.core.utils.RestUtils;
import org.openmbee.sdvc.data.domains.global.Branch;
import org.openmbee.sdvc.twc.TeamworkCloud;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import java.util.Set;

public class TwcBranchPermissionsDelegate implements PermissionsDelegate{

    private RestUtils restUtils;

    private Branch branch;
    private TeamworkCloud teamworkCloud;
    private String workspaceId;
    private String resourceId;

    public TwcBranchPermissionsDelegate(Branch branch, TeamworkCloud teamworkCloud, String workspaceId, String resourceId) {
        this.branch = branch;
        this.teamworkCloud = teamworkCloud;
        this.workspaceId = workspaceId;
        this.resourceId = resourceId;
    }

    @Autowired
    public void setRestUtils(RestUtils restUtils) {
        this.restUtils = restUtils;
    }

    @Override
    public boolean hasPermission(String user, Set<String> groups, String privilege) {
        //TODO implement with calls to TWC to determine permissions
        return false;
    }

    @Override
    public void initializePermissions(String creator) {
        //Do nothing, permissions are already initialized in TWC
    }

    @Override
    public void initializePermissions(String creator, boolean inherit) {
        //Do nothing, permissions are already initialized in TWC
    }

    @Override
    public boolean setInherit(boolean isInherit) {
        //Do nothing, permission inheritance will be handled by TWC
        return false;
    }

    @Override
    public void setPublic(boolean isPublic) {
        forbidden();
    }

    @Override
    public PermissionUpdateResponse updateUserPermissions(PermissionUpdateRequest req) {
        forbidden();
        return new PermissionUpdateResponseBuilder().getPermissionUpdateResponse();
    }

    @Override
    public PermissionUpdateResponse updateGroupPermissions(PermissionUpdateRequest req) {
        forbidden();
        return new PermissionUpdateResponseBuilder().getPermissionUpdateResponse();
    }

    @Override
    public PermissionResponse getUserRoles() {
        forbidden();
        return null;
    }

    @Override
    public PermissionResponse getGroupRoles() {
        forbidden();
        return null;
    }

    @Override
    public PermissionUpdatesResponse recalculateInheritedPerms() {
        //Do nothing, permission inheritance will be handled by TWC
        return new PermissionUpdatesResponseBuilder().getPermissionUpdatesReponse();
    }

    public Branch getBranch() {
        return branch;
    }

    public TeamworkCloud getTeamworkCloud() {
        return teamworkCloud;
    }

    public String getWorkspaceId() {
        return workspaceId;
    }

    public String getResourceId() {
        return resourceId;
    }

    private void forbidden() {
        throw new TwcPermissionException(HttpStatus.FORBIDDEN,
            String.format("Permissions for org %s (%s), project %s (%s), branch % is controlled by Teamwork Cloud (%s)",
                branch.getProject().getOrganization().getOrganizationName(),
                branch.getProject().getOrganization().getOrganizationId(),
                branch.getProject().getProjectName(),
                branch.getProject().getProjectId(),
                branch.getBranchId(),
                teamworkCloud.getUrl()));
    }
}
