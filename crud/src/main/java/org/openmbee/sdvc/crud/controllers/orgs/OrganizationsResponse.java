package org.openmbee.sdvc.crud.controllers.orgs;

import java.util.ArrayList;
import java.util.List;
import org.openmbee.sdvc.crud.controllers.BaseResponse;
import org.openmbee.sdvc.crud.controllers.Constants;
import org.openmbee.sdvc.json.OrgJson;

public class OrganizationsResponse extends BaseResponse {

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
