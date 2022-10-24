package org.openmbee.mms.core.objects;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

import org.openmbee.mms.json.ElementJson;

public class ElementsRequest extends BaseRequest {

    @Schema(required = true)
    private List<ElementJson> elements;

    private List<ElementJson> deletes;

    public List<ElementJson> getElements() {
        return elements;
    }

    public void setElements(List<ElementJson> elements) {
        this.elements = elements;
    }

    public List<ElementJson> getDeletes() {
        return deletes;
    }

    public void setDeletes(List<ElementJson> deletes) {
        this.deletes = deletes;
    }
}
