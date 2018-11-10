package org.openmbee.sdvc.crud.controllers.orgs;

import java.util.List;
import org.openmbee.sdvc.crud.controllers.BaseRequest;

public class OrganizationsRequest extends BaseRequest {

    private List<OrgJson> orgs;

    public List<OrgJson> getOrgs() {
        return orgs;
    }
    public void setOrgs(List<OrgJson> orgs) {
        this.orgs = orgs;
    }

}
