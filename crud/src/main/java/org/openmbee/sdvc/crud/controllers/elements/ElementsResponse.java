package org.openmbee.sdvc.crud.controllers.elements;

import java.util.ArrayList;
import java.util.List;
import org.openmbee.sdvc.crud.controllers.BaseResponse;
import org.openmbee.sdvc.crud.controllers.Constants;
import org.openmbee.sdvc.json.ElementJson;

public class ElementsResponse extends BaseResponse {

    public ElementsResponse() {
        this.put(Constants.ELEMENT_KEY, new ArrayList<ElementJson>());
    }

    public List<ElementJson> getElements() {
        return (List<ElementJson>) this.get(Constants.ELEMENT_KEY);
    }

    public void setElements(List<ElementJson> elements) {
        this.put(Constants.ELEMENT_KEY, elements);
    }

}
