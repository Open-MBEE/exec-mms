package org.openmbee.sdvc.crud.controllers.elements;

import java.util.List;
import org.openmbee.sdvc.crud.controllers.BaseJson;

public class ElementJson extends BaseJson {

    public static final String INREFIDS = "_inRefIds";

    public List<String> getInRefIds() {
        return (List<String>) this.get(INREFIDS);
    }

    public ElementJson setInRefIds(List<String> inRefIds) {
        this.put(INREFIDS, inRefIds);
        return this;
    }
}
