package org.openmbee.mms.groups.objects;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public class GroupUpdateRequest {

    @Schema(required = true)
    public Action action;

    @Schema(required = true)
    public List<String> users;

    public Action getAction() {
        return action;
    }

    public void setAction(Action action) {
        this.action = action;
    }

    public List<String> getUsers() {
        return users;
    }

    public void setUsers(List<String> users) {
        this.users = users;
    }
}
