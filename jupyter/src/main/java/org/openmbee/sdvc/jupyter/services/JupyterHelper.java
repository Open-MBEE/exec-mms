package org.openmbee.sdvc.jupyter.services;

import org.openmbee.sdvc.json.ElementJson;
import org.openmbee.sdvc.jupyter.JupyterConstants;
import org.openmbee.sdvc.jupyter.JupyterNodeType;
import org.springframework.stereotype.Component;

@Component
public class JupyterHelper {

    public static JupyterNodeType getNodeType(ElementJson e) {
        if (e.containsKey(JupyterConstants.CELLTYPE))
            return JupyterNodeType.CELL;
        return JupyterNodeType.NOTEBOOK;
    }

}
