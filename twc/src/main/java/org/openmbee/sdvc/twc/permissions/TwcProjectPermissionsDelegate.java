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
import org.openmbee.sdvc.data.domains.global.Project;
import org.openmbee.sdvc.twc.TeamworkCloud;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import java.util.Set;

public class TwcProjectPermissionsDelegate implements PermissionsDelegate {

    private RestUtils restUtils;

    private Project project;
    private TeamworkCloud teamworkCloud;
    private String workspaceId;
    private String resourceId;

    public TwcProjectPermissionsDelegate(Project project, TeamworkCloud teamworkCloud, String workspaceId, String resourceId) {
        this.project = project;
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
        //TODO: implement with calls to TWC to determine permissions
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
        forbidden();
        return new PermissionUpdatesResponseBuilder().getPermissionUpdatesReponse();
    }

    public Project getProject() {
        return project;
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
            String.format("Permissions for org %s (%s), project %s (%s) is controlled by Teamwork Cloud (%s)",
                project.getOrganization().getOrganizationName(),
                project.getOrganization().getOrganizationId(),
                project.getProjectName(),
                project.getProjectId(),
                teamworkCloud.getUrl()));
    }
}
