package org.openmbee.sdvc.crud.controllers.branches;

import java.util.ArrayList;
import java.util.List;
import org.openmbee.sdvc.crud.domains.Branch;
import org.openmbee.sdvc.crud.controllers.BaseResponse;

public class BranchesResponse extends BaseResponse {

    private List<Branch> branches = new ArrayList<>();

    public BranchesResponse() {
    }

    public List<Branch> getBranches() {
        return branches;
    }

    public void setBranches(List<Branch> branches) {
        this.branches = branches;
    }

}
