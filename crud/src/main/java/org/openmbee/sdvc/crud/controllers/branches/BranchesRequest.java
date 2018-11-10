package org.openmbee.sdvc.crud.controllers.branches;

import java.util.List;
import org.openmbee.sdvc.crud.controllers.BaseRequest;

public class BranchesRequest extends BaseRequest {

    private List<RefJson> branches;

    public List<RefJson> getBranches() {
        return branches;
    }
    public void setBranches(List<RefJson> branches) {
        this.branches = branches;
    }


}
