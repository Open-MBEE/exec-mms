package org.openmbee.mms.groups.objects;

import io.swagger.v3.oas.annotations.media.Schema;
import org.openmbee.mms.core.objects.BaseResponse;
import org.openmbee.mms.core.objects.Rejection;
import org.openmbee.mms.json.GroupJson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class GroupsResponse extends BaseResponse<GroupsResponse> {

    @Schema(required = true)
    private List<GroupJson> groups;

    public GroupsResponse() {
        this.groups = new ArrayList<>();
    }

    public List<GroupJson> getGroups() {
        return this.groups;
    }

    public void setGroups(List<GroupJson> groups) {
        this.groups = groups;
    }

}
