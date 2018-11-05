package org.openmbee.sdvc.crud.controllers.elements;

import java.util.ArrayList;
import java.util.List;
import org.openmbee.sdvc.crud.controllers.BaseRequest;

public class ElementsPostRequest extends BaseRequest {

    private List<Element> elements = new ArrayList<>();

    public ElementsPostRequest() {
    }

    public List<Element> getElements() {
        return elements;
    }

    public void setElements(List<Element> elements) {
        this.elements = elements;
    }

}
