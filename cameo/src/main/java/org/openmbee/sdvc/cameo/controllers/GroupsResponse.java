package org.openmbee.sdvc.cameo.controllers;

import java.util.ArrayList;
import java.util.List;
import org.openmbee.sdvc.core.objects.BaseResponse;
import org.openmbee.sdvc.json.ElementJson;

public class GroupsResponse extends BaseResponse<GroupsResponse> {

    private List<ElementJson> groups;

    public GroupsResponse() {
        this.groups = new ArrayList<>();
    }

    public List<ElementJson> getGroups() {
        return groups;
    }

    public GroupsResponse setGroups(List<ElementJson> groups) {
        this.groups = groups;
        return this;
    }
}
