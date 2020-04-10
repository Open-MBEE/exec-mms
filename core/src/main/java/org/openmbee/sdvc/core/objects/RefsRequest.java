package org.openmbee.sdvc.core.objects;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

import org.openmbee.sdvc.json.RefJson;

public class RefsRequest extends BaseRequest {

    private List<RefJson> refs;

    @Schema(required=true)
    public List<RefJson> getRefs() {
        return refs;
    }

    public void setRefs(List<RefJson> refs) {
        this.refs = refs;
    }

}
