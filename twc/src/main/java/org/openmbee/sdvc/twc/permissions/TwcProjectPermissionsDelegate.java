package org.openmbee.sdvc.twc.permissions;

import org.openmbee.sdvc.core.delegation.PermissionsDelegate;
import org.openmbee.sdvc.core.objects.PermissionResponse;
import org.openmbee.sdvc.core.objects.PermissionUpdateRequest;
import org.openmbee.sdvc.core.utils.RestUtils;
import org.openmbee.sdvc.data.domains.global.Project;
import org.openmbee.sdvc.twc.TeamworkCloud;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Set;

public class TwcProjectPermissionsDelegate implements PermissionsDelegate {

    @Autowired
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
        //TODO should probably throw an error so users know they can't modify permissions from here
    }

    @Override
    public void updateUserPermissions(PermissionUpdateRequest req) {
        //TODO should probably throw an error so users know they can't modify permissions from here: they must use TWC
    }

    @Override
    public void updateGroupPermissions(PermissionUpdateRequest req) {
        //TODO should probably throw an error so users know they can't modify permissions from here: they must use TWC
    }

    @Override
    public PermissionResponse getUserRoles() {
        //TODO should probably throw so users know they can't view all permissions from here (assuming this is only used for admin purposes)
        return null;
    }

    @Override
    public PermissionResponse getGroupRoles() {
        //TODO should probably throw so users know they can't view all permissions from here (assuming this is only used for admin purposes)
        return null;
    }

    @Override
    public void recalculateInheritedPerms() {
        //Do nothing, permission inheritance will be handled by TWC
    }
}
