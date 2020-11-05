package org.openmbee.mms.json;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.AccessMode;
import java.util.List;

@Schema(name = "Element")
public class ElementJson extends BaseJson<ElementJson> {

    public static final String INREFIDS = "_inRefIds";

    @JsonProperty(INREFIDS)
    @Schema(accessMode = AccessMode.READ_ONLY)
    public List<String> getInRefIds() {
        return (List<String>) this.get(INREFIDS);
    }

    @JsonProperty(INREFIDS)
    public ElementJson setInRefIds(List<String> inRefIds) {
        this.put(INREFIDS, inRefIds);
        return this;
    }

}
