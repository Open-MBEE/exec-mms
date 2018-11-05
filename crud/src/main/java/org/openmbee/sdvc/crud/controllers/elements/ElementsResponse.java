package org.openmbee.sdvc.crud.controllers.elements;

import java.util.ArrayList;
import java.util.List;
import org.openmbee.sdvc.crud.controllers.BaseResponse;
import org.openmbee.sdvc.crud.domains.Node;

public class ElementsResponse extends BaseResponse {

    private List<Node> elements = new ArrayList<>();

    public ElementsResponse() {
    }

    public ElementsResponse(Node element) {
        elements.add(element);
    }

    public ElementsResponse(List<Node> elements) {
        setElements(elements);
    }

    public List<Node> getElements() {
        return elements;
    }

    public void setElements(List<Node> elements) {
        this.elements.addAll(elements);
    }

}
