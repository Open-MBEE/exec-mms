package org.openmbee.sdvc.core.builders;

import org.openmbee.sdvc.core.objects.PermissionUpdateResponse;
import org.openmbee.sdvc.core.objects.PermissionUpdatesResponse;

public class PermissionUpdatesResponseBuilder {

    private Boolean inherit;
    private Boolean isPublic;
    private PermissionUpdateResponseBuilder usersBuilder = new PermissionUpdateResponseBuilder();
    private PermissionUpdateResponseBuilder groupsBuilder = new PermissionUpdateResponseBuilder();

    public PermissionUpdatesResponseBuilder setInherit(Boolean inherit) {
        this.inherit = inherit;
        return this;
    }

    public PermissionUpdatesResponseBuilder setPublic(Boolean aPublic) {
        isPublic = aPublic;
        return this;
    }

    public PermissionUpdatesResponseBuilder addUsers(PermissionUpdateResponse users) {
        usersBuilder.add(users);
        return this;
    }

    public PermissionUpdatesResponseBuilder addGroups(PermissionUpdateResponse groups) {
        groupsBuilder.add(groups);
        return this;
    }

    public PermissionUpdatesResponseBuilder add(PermissionUpdatesResponse permissionUpdatesResponse) {
        this.inherit = this.inherit || permissionUpdatesResponse.getInherit();
        this.isPublic = this.isPublic || permissionUpdatesResponse.getPublic();
        this.addUsers(permissionUpdatesResponse.getUsers());
        this.addGroups(permissionUpdatesResponse.getGroups());
        return this;
    }

    public PermissionUpdatesResponse getPermissionUpdatesReponse() {
        PermissionUpdatesResponse permissionUpdatesResponse = new PermissionUpdatesResponse();
        permissionUpdatesResponse.setInherit(this.inherit);
        permissionUpdatesResponse.setPublic(this.isPublic);
        permissionUpdatesResponse.setUsers(usersBuilder.getPermissionUpdateReponse());
        permissionUpdatesResponse.setGroups(groupsBuilder.getPermissionUpdateReponse());
        return permissionUpdatesResponse;
    }

}
