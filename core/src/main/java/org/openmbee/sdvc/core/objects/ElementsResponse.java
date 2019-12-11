package org.openmbee.sdvc.core.objects;

import java.util.ArrayList;
import java.util.List;

import org.openmbee.sdvc.core.config.Constants;
import org.openmbee.sdvc.json.ElementJson;

public class ElementsResponse extends BaseResponse<ElementsResponse> {

    private List<ElementJson> elements;

    public ElementsResponse() {
        this.elements = new ArrayList<>();
    }

    public List<ElementJson> getElements() {
        return elements;
    }

    public ElementsResponse setElements(List<ElementJson> elements) {
        this.elements = elements;
        return this;
    }
}
