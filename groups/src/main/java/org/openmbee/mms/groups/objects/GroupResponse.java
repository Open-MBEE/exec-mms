package org.openmbee.mms.groups.objects;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;
import java.util.stream.Collectors;

import org.openmbee.mms.data.domains.global.Group;
import org.openmbee.mms.data.domains.global.User;

public class GroupResponse {

    @Schema(required = true)
    private String group;

    @Schema(nullable = true)
    private List<String> users;

    public GroupResponse(){}

    public GroupResponse(Group group){
        this.group = group.getName();
        this.users = group.getUsers().stream().map(User::getUsername).collect(Collectors.toList());
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public List<String> getUsers() {
        return users;
    }

    public void setUsers(List<String> users) {
        this.users = users;
    }
}
