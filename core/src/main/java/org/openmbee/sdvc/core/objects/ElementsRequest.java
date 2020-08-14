package org.openmbee.sdvc.core.objects;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

import org.openmbee.sdvc.json.ElementJson;

public class ElementsRequest extends BaseRequest {

    @Schema(required = true)
    private List<ElementJson> elements;

    public List<ElementJson> getElements() {
        return elements;
    }

    public void setElements(List<ElementJson> elements) {
        this.elements = elements;
    }
}
