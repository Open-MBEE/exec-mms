package org.openmbee.mms.crud.domain;

import org.openmbee.mms.json.ElementJson;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JsonDomain {

    public Map<String, ElementJson> convertJsonToMap(List<ElementJson> elements) {
        if (elements == null || elements.isEmpty()) {
            return Collections.emptyMap();
        }
        Map<String, ElementJson> result = new HashMap<>();
        
        for (ElementJson elem : elements) {
            if (elem == null) {
                continue;
            }
            if (elem.getId() != null && !elem.getId().equals("")) {
                result.put(elem.getId(), elem);
            }
        }
        return result;
    }

}
