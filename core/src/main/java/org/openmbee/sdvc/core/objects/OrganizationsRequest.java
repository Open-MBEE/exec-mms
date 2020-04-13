package org.openmbee.sdvc.core.objects;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

import org.openmbee.sdvc.json.OrgJson;

public class OrganizationsRequest extends BaseRequest {

    @Schema(required = true)
    private List<OrgJson> orgs;

    public List<OrgJson> getOrgs() {
        return orgs;
    }

    public void setOrgs(List<OrgJson> orgs) {
        this.orgs = orgs;
    }

}
