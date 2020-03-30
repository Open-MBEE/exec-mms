package org.openmbee.sdvc.core.objects;


import org.openmbee.sdvc.core.objects.PermissionUpdateResponse;

public class PermissionUpdatesResponse {

    private Boolean inherit;
    private Boolean isPublic;
    private PermissionUpdateResponse users;
    private PermissionUpdateResponse groups;

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

    public PermissionUpdateResponse getUsers() {
        return users;
    }

    public void setUsers(PermissionUpdateResponse users) {
        this.users = users;
    }

    public PermissionUpdateResponse getGroups() {
        return groups;
    }

    public void setGroups(PermissionUpdateResponse groups) {
        this.groups = groups;
    }



}
