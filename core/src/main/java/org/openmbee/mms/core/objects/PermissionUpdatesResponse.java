package org.openmbee.mms.core.objects;


import com.fasterxml.jackson.annotation.JsonInclude;

public class PermissionUpdatesResponse {

    private Boolean inherit;
    private Boolean isPublic;
    private PermissionUpdateResponse users;
    private PermissionUpdateResponse groups;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public Boolean getInherit() {
        return inherit;
    }

    public void setInherit(Boolean inherit) {
        this.inherit = inherit;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
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
