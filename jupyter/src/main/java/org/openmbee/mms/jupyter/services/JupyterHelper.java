package org.openmbee.mms.jupyter.services;

import org.openmbee.mms.json.ElementJson;
import org.openmbee.mms.jupyter.JupyterConstants;
import org.openmbee.mms.jupyter.JupyterNodeType;
import org.springframework.stereotype.Component;

@Component
public class JupyterHelper {

    public static JupyterNodeType getNodeType(ElementJson e) {
        if (e.containsKey(JupyterConstants.CELLTYPE))
            return JupyterNodeType.CELL;
        return JupyterNodeType.NOTEBOOK;
    }

}
