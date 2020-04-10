package org.openmbee.sdvc.core.objects;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.ArrayList;
import java.util.List;

import org.openmbee.sdvc.json.ElementJson;

public class ElementsResponse extends BaseResponse<ElementsResponse> {

    @Schema(required=true)
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
