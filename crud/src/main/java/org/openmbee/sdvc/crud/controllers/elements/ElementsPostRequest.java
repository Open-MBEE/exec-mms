package org.openmbee.sdvc.crud.controllers.elements;

import java.util.List;

import org.openmbee.sdvc.crud.controllers.BaseRequest;
import org.openmbee.sdvc.crud.controllers.Constants;

public class ElementsPostRequest extends BaseRequest {

    public List<ElementJson> getElements() {
        return (List<ElementJson>) this.get(Constants.ELEMENT_KEY);
    }

    public void setElements(List<ElementJson> elements) {
        this.put(Constants.ELEMENT_KEY, elements);
    }
}
