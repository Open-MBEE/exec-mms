package org.openmbee.mms.permissions;

import org.openmbee.mms.core.objects.PermissionResponse;

public class PermissionsResponse {

    private Boolean inherit;
    private Boolean isPublic;
    private PermissionResponse users;
    private PermissionResponse groups;

    public Boolean getInherit() {
        return inherit;
    }

    public void setInherit(Boolean inherit) {
        this.inherit = inherit;
    }

    public Boolean getPublic() {
        return isPublic;
    }

    public void setPublic(Boolean aPublic) {
        isPublic = aPublic;
    }

    public PermissionResponse getUsers() {
        return users;
    }

    public void setUsers(PermissionResponse users) {
        this.users = users;
    }

    public PermissionResponse getGroups() {
        return groups;
    }

    public void setGroups(PermissionResponse groups) {
        this.groups = groups;
    }



}
