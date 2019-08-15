package org.openmbee.sdvc.core.objects;

import java.util.ArrayList;
import java.util.List;

import org.openmbee.sdvc.core.config.Constants;
import org.openmbee.sdvc.json.OrgJson;

public class OrganizationsResponse extends BaseResponse<OrganizationsResponse> {

    public OrganizationsResponse() {
        this.put(Constants.ORGANIZATION_KEY, new ArrayList<OrgJson>());
    }

    public List<OrgJson> getOrgs() {
        return (List<OrgJson>) this.get(Constants.ORGANIZATION_KEY);
    }

    public void setOrgs(List<OrgJson> orgs) {
        this.put(Constants.ORGANIZATION_KEY, orgs);
    }
}
