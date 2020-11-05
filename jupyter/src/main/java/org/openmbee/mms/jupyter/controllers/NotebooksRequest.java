package org.openmbee.mms.jupyter.controllers;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import org.openmbee.mms.core.objects.ElementsRequest;
import org.openmbee.mms.json.ElementJson;

public class NotebooksRequest extends ElementsRequest {

    @Schema(required = true)
    private List<ElementJson> notebooks;

    public List<ElementJson> getNotebooks() {
        return notebooks;
    }

    public void setNotebooks(List<ElementJson> notebooks) {
        this.notebooks = notebooks;
    }

    @Override
    @JsonIgnore
    public List<ElementJson> getElements() {
        return notebooks;
    }

    @Override
    public void setElements(List<ElementJson> elements) {
        this.notebooks = elements;
    }
}
