package org.openmbee.mms.core.objects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

import org.openmbee.mms.json.RefJson;

@JsonIgnoreProperties({"source", "comment"})
public class RefsRequest extends BaseRequest {

    private List<RefJson> refs;

    @Schema(required = true)
    public List<RefJson> getRefs() {
        return refs;
    }

    public void setRefs(List<RefJson> refs) {
        this.refs = refs;
    }

}
