package org.openmbee.sdvc.crud.controllers.branches;

import java.util.List;
import org.openmbee.sdvc.crud.controllers.BaseRequest;
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
