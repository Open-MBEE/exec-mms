package org.openmbee.mms.groups.objects;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public class GroupsResponse {

    @Schema(required = true)
    private List<String> groups;

    public List<String> getGroups() {
        return groups;
    }

    public void setGroups(List<String> groups) {
        this.groups = groups;
    }
}
