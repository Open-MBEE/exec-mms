package org.openmbee.sdvc.core.objects;

import java.util.ArrayList;
import java.util.List;

import org.openmbee.sdvc.core.config.Constants;
import org.openmbee.sdvc.json.RefJson;

public class BranchesResponse extends BaseResponse<BranchesResponse> {

    public BranchesResponse() {
        this.put(Constants.BRANCH_KEY, new ArrayList<RefJson>());
    }

    public List<RefJson> getBranches() {
        return (List<RefJson>) this.get(Constants.BRANCH_KEY);
    }

    public void setBranches(List<RefJson> refs) {
        this.put(Constants.BRANCH_KEY, refs);
    }

}