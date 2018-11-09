package org.openmbee.sdvc.crud.controllers.orgs;

import java.util.List;
import org.openmbee.sdvc.crud.controllers.BaseRequest;
import org.openmbee.sdvc.crud.controllers.Constants;

public class OrganizationsRequest extends BaseRequest {

    public List<OrgJson> getOrgs() {
        return (List<OrgJson>) this.get(Constants.ORGANIZATION_KEY);
    }
    public void setOrgs(List<OrgJson> orgs) {
        this.put(Constants.ORGANIZATION_KEY, orgs);
    }

}
