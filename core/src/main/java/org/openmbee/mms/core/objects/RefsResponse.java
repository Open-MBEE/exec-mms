package org.openmbee.mms.core.objects;

import java.util.ArrayList;
import java.util.List;

import org.openmbee.mms.json.RefJson;

public class RefsResponse extends BaseResponse<RefsResponse> {

    private List<RefJson> refs;

    public RefsResponse() {
        this.refs = new ArrayList<>();
    }

    public List<RefJson> getRefs() {
        return refs;
    }

    public RefsResponse setRefs(List<RefJson> refs) {
        this.refs = refs;
        return this;
    }

}
