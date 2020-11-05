package org.openmbee.mms.core.objects;

import java.util.ArrayList;
import java.util.List;

import org.openmbee.mms.json.ElementJson;

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
