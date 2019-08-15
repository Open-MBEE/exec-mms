package org.openmbee.sdvc.core.objects;

import java.util.ArrayList;
import java.util.List;

import org.openmbee.sdvc.core.config.Constants;
import org.openmbee.sdvc.json.ElementJson;

public class ElementsResponse extends BaseResponse<ElementsResponse> {

    public ElementsResponse() {
        this.put(Constants.ELEMENT_KEY, new ArrayList<ElementJson>());
    }

    public List<ElementJson> getElements() {
        return (List<ElementJson>) this.get(Constants.ELEMENT_KEY);
    }

    public ElementsResponse setElements(List<ElementJson> elements) {
        this.put(Constants.ELEMENT_KEY, elements);
        return this;
    }


}
