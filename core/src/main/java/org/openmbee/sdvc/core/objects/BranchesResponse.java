package org.openmbee.sdvc.core.objects;

import java.util.ArrayList;
import java.util.List;

import org.openmbee.sdvc.core.config.Constants;
import org.openmbee.sdvc.json.RefJson;

public class BranchesResponse extends BaseResponse<BranchesResponse> {

    private List<RefJson> refs;

    public BranchesResponse() {
        this.refs = new ArrayList<>();
    }

    public List<RefJson> getRefs() {
        return refs;
    }

    public BranchesResponse setRefs(List<RefJson> refs) {
        this.refs = refs;
        return this;
    }

}
