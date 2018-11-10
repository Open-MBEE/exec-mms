package org.openmbee.sdvc.crud.controllers.elements;

import org.openmbee.sdvc.crud.controllers.BaseRequest;

import java.util.List;

public class ElementsRequest extends BaseRequest {

    private List<ElementJson> elements;

    public List<ElementJson> getElements() {
        return elements;
    }

    public void setElements(List<ElementJson> elements) {
        this.elements = elements;
    }
}
