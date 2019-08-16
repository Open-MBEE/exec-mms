package org.openmbee.sdvc.core.objects;

import java.util.List;

import org.openmbee.sdvc.json.RefJson;

public class BranchesRequest extends BaseRequest {

    private List<RefJson> refs;

    public List<RefJson> getRefs() {
        return refs;
    }

    public void setRefs(List<RefJson> refs) {
        this.refs = refs;
    }


}
