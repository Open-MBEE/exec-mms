package org.openmbee.sdvc.core.objects;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.ArrayList;
import java.util.List;

import org.openmbee.sdvc.json.OrgJson;

public class OrganizationsResponse extends BaseResponse<OrganizationsResponse> {

    @Schema(required = true)
    private List<OrgJson> orgs;

    public OrganizationsResponse() {
        this.orgs = new ArrayList<>();
    }

    public List<OrgJson> getOrgs() {
        return orgs;
    }

    public void setOrgs(List<OrgJson> orgs) {
        this.orgs = orgs;
    }
}
