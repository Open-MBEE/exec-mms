package org.openmbee.sdvc.crud.controllers.elements;

import java.util.List;
import org.openmbee.sdvc.crud.controllers.BaseRequest;

public class ElementsRequest extends BaseRequest {

    private List<ElementJson> elements;

    public List<ElementJson> getElements() {
        return elements;
    }

    public void setElements(List<ElementJson> elements) {
        this.elements = elements;
    }
}
