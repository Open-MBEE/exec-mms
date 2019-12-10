package org.openmbee.sdvc.json;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class ElementJson extends BaseJson<ElementJson> {

    public static final String INREFIDS = "_inRefIds";

    @JsonProperty(INREFIDS)
    public List<String> getInRefIds() {
        return (List<String>) this.get(INREFIDS);
    }

    @JsonProperty(INREFIDS)
    public ElementJson setInRefIds(List<String> inRefIds) {
        this.put(INREFIDS, inRefIds);
        return this;
    }
}
