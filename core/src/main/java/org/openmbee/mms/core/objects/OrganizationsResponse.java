package org.openmbee.mms.core.objects;

import java.util.ArrayList;
import java.util.List;

import org.openmbee.mms.json.OrgJson;

public class OrganizationsResponse extends BaseResponse<OrganizationsResponse> {

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
