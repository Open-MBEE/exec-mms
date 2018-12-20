package org.openmbee.sdvc.crud.controllers.elements;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.openmbee.sdvc.crud.controllers.BaseResponse;
import org.openmbee.sdvc.crud.controllers.Constants;

public class ElementsResponse extends BaseResponse {

    public ElementsResponse() {
        this.put(Constants.ELEMENT_KEY, new ArrayList<Map>());
    }

    public List<Map> getElements() {
        return (List<Map>) this.get(Constants.ELEMENT_KEY);
    }

    public void setElements(List<Map> elements) {
        this.put(Constants.ELEMENT_KEY, elements);
    }

    public List<Map> getRejected() {
        return (List<Map>) this.get(Constants.REJECTED);
    }

    public void setRejected(List<Map> rejected) {
        this.put(Constants.REJECTED, rejected);
    }
}
