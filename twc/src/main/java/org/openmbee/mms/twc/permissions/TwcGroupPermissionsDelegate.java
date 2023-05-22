package org.openmbee.mms.twc.permissions;

import org.openmbee.mms.core.delegation.PermissionsDelegate;
import org.openmbee.mms.core.objects.PermissionResponse;
import org.openmbee.mms.core.objects.PermissionUpdateRequest;
import org.openmbee.mms.core.objects.PermissionUpdateResponse;
import org.openmbee.mms.core.objects.PermissionUpdatesResponse;
import org.openmbee.mms.data.domains.global.Group;
import org.openmbee.mms.federatedpersistence.permissions.AbstractDefaultPermissionsDelegate;
import org.openmbee.mms.json.GroupJson;
import org.openmbee.mms.twc.exceptions.TwcConfigurationException;
import org.springframework.http.HttpStatus;

import java.util.Set;


public class TwcGroupPermissionsDelegate implements PermissionsDelegate {
    private final GroupJson group;

    public TwcGroupPermissionsDelegate(GroupJson group) {
        this.group = group;
    }

    @Override
    public boolean hasPermission(String user, Set<String> groups, String privilege) {
        return false;
    }

    @Override
    public boolean hasGroupPermissions(String group, String privilege) {
        return false;
    }

    @Override
    public void initializePermissions(String creator) {

    }

    @Override
    public void initializePermissions(String creator, boolean inherit) {
        if(inherit) {
            throw new IllegalArgumentException("Cannot inherit permissions for a Group");
        }

    }

    @Override
    public boolean setInherit(boolean isInherit) {
        if(isInherit) {
            throw new IllegalArgumentException("Cannot inherit permissions for a Group");
        }
        return false;
    }

    @Override
    public PermissionResponse getInherit() {
        //Groups will not inherit
        return PermissionResponse.getDefaultResponse();
    }

    @Override
    public void setPublic(boolean isPublic) {
        throw new TwcConfigurationException(HttpStatus.BAD_REQUEST,
            "Cannot Modify Group.  Groups for this server are controlled by Teamwork Cloud");
    }

    @Override
    public PermissionUpdateResponse updateUserPermissions(PermissionUpdateRequest req) {
        throw new TwcConfigurationException(HttpStatus.BAD_REQUEST,
            "Cannot Modify Group.  Groups for this server are controlled by Teamwork Cloud");
    }

    @Override
    public PermissionUpdateResponse updateGroupPermissions(PermissionUpdateRequest req) {
        throw new TwcConfigurationException(HttpStatus.BAD_REQUEST,
            "Cannot Modify Group.  Groups for this server are controlled by Teamwork Cloud");
    }

    @Override
    public PermissionResponse getUserRoles() {
        throw new TwcConfigurationException(HttpStatus.BAD_REQUEST,
            "Cannot Query User Roles.  Permissions for this group are controlled by Teamwork Cloud");
    }

    @Override
    public PermissionResponse getGroupRoles() {
        throw new TwcConfigurationException(HttpStatus.BAD_REQUEST,
            "Cannot Query Group Roles.  Permissions for this group are controlled by Teamwork Cloud");
    }

    @Override
    public PermissionUpdatesResponse recalculateInheritedPerms() {
        return null;
    }
}
