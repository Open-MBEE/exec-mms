package org.openmbee.sdvc.crud.controllers.orgs;

import java.util.ArrayList;
import java.util.List;
import org.openmbee.sdvc.core.domains.Organization;
import org.openmbee.sdvc.crud.controllers.BaseRequest;

public class OrganizationsRequest extends BaseRequest {

    private List<Organization> orgs = new ArrayList<>();

    public OrganizationsRequest() {
    }

    public List<Organization> getOrgs() {
        return orgs;
    }

    public void setOrgs(List<Organization> orgs) {
        this.orgs = orgs;
    }

}
