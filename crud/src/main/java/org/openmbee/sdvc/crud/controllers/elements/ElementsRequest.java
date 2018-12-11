package org.openmbee.sdvc.crud.controllers.elements;

import java.util.List;
import org.openmbee.sdvc.crud.controllers.BaseRequest;
import org.openmbee.sdvc.json.ElementJson;

public class ElementsRequest extends BaseRequest {

    private List<ElementJson> elements;

    public List<ElementJson> getElements() {
        return elements;
    }

    public void setElements(List<ElementJson> elements) {
        this.elements = elements;
    }
}
