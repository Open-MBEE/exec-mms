package org.openmbee.sdvc.jupyter.controllers;

import java.util.ArrayList;
import java.util.List;
import org.openmbee.sdvc.core.objects.BaseResponse;
import org.openmbee.sdvc.json.ElementJson;
import org.openmbee.sdvc.jupyter.JupyterConstants;

public class NotebooksResponse extends BaseResponse<NotebooksResponse> {

    private List<ElementJson> notebooks;

    public NotebooksResponse() {
        this.notebooks = new ArrayList<>();
    }

    public List<ElementJson> getNotebooks() {
        return notebooks;
    }

    public NotebooksResponse setNotebooks(List<ElementJson> notebooks) {
        this.notebooks = notebooks;
        return this;
    }
}
