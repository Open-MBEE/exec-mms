package org.openmbee.sdvc.crud.controllers.elements;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.openmbee.sdvc.crud.controllers.BaseResponse;
import org.openmbee.sdvc.crud.controllers.Constants;
import org.openmbee.sdvc.json.ElementJson;

public class ElementsResponse extends BaseResponse<ElementsResponse> {

    public ElementsResponse() {
        this.put(Constants.ELEMENT_KEY, new ArrayList<ElementJson>());
    }

    public List<ElementJson> getElements() {
        return (List<ElementJson>) this.get(Constants.ELEMENT_KEY);
    }

    public void setElements(List<ElementJson> elements) {
        this.put(Constants.ELEMENT_KEY, elements);
    }

    public List<Map> getRejected() {
        return (List<Map>) this.get(Constants.REJECTED);
    }

    public void setRejected(List<Map> rejected) {
        this.put(Constants.REJECTED, rejected);
    }
}
