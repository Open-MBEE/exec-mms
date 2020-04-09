package org.openmbee.sdvc.core.objects;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.ArrayList;
import java.util.List;

import org.openmbee.sdvc.json.RefJson;

public class BranchesResponse extends BaseResponse<BranchesResponse> {

    @Schema(required=true)
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
