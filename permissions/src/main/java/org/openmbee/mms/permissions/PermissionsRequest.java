package org.openmbee.mms.permissions;

import org.openmbee.mms.core.objects.PermissionUpdateRequest;

public class PermissionsRequest {

    private PermissionUpdateRequest users;
    private PermissionUpdateRequest groups;
    private Boolean inherit;
    private Boolean isPublic;

    public PermissionUpdateRequest getUsers() {
        return users;
    }

    public void setUsers(PermissionUpdateRequest users) {
        this.users = users;
    }

    public PermissionUpdateRequest getGroups() {
        return groups;
    }

    public void setGroups(PermissionUpdateRequest groups) {
        this.groups = groups;
    }

    public Boolean getInherit() {
        return inherit;
    }

    public void setInherit(Boolean inherit) {
        this.inherit = inherit;
    }

    public Boolean getPublic() {
        return isPublic;
    }

    public void setPublic(Boolean isPublic) {
        this.isPublic = isPublic;
    }

}
