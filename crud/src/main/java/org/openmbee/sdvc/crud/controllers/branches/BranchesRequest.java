package org.openmbee.sdvc.crud.controllers.branches;

import java.util.ArrayList;
import java.util.List;
import org.openmbee.sdvc.crud.controllers.BaseRequest;
import org.openmbee.sdvc.crud.domains.Branch;

public class BranchesRequest extends BaseRequest {

    private List<Branch> branches = new ArrayList<>();

    public BranchesRequest() {
    }

    public List<Branch> getBranches() {
        return branches;
    }

    public void setBranches(List<Branch> projects) {
        this.branches = projects;
    }

}
