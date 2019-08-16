package org.openmbee.sdvc.jupyter.controllers;

import java.util.ArrayList;
import java.util.List;
import org.openmbee.sdvc.core.objects.BaseResponse;
import org.openmbee.sdvc.json.ElementJson;
import org.openmbee.sdvc.jupyter.JupyterConstants;

public class NotebooksResponse extends BaseResponse<NotebooksResponse> {

    public NotebooksResponse() {
        this.put(JupyterConstants.NOTEBOOKS, new ArrayList<ElementJson>());
    }

    public List<ElementJson> getNotebooks() {
        return (List<ElementJson>) this.get(JupyterConstants.NOTEBOOKS);
    }

    public NotebooksResponse setNotebooks(List<ElementJson> elements) {
        this.put(JupyterConstants.NOTEBOOKS, elements);
        return this;
    }
}
