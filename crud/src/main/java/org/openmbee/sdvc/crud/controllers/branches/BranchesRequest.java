package org.openmbee.sdvc.crud.controllers.branches;

import java.util.List;
import org.openmbee.sdvc.crud.controllers.BaseRequest;
import org.openmbee.sdvc.crud.controllers.Constants;

public class BranchesRequest extends BaseRequest {

    public List<RefJson> getBranches() {
        return (List<RefJson>) this.get(Constants.BRANCH_KEY);
    }
    public void setBranches(List<RefJson> branches) {
        this.put(Constants.BRANCH_KEY, branches);
    }


}
