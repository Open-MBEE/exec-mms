package org.openmbee.sdvc.json;

import java.util.List;

public class ElementJson extends BaseJson<ElementJson> {

    public static final String INREFIDS = "_inRefIds";

    public List<String> getInRefIds() {
        return (List<String>) this.get(INREFIDS);
    }

    public ElementJson setInRefIds(List<String> inRefIds) {
        this.put(INREFIDS, inRefIds);
        return this;
    }
}
