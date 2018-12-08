package org.openmbee.sdvc.sysml.services;

import org.openmbee.sdvc.crud.controllers.elements.ElementJson;
import org.openmbee.sdvc.crud.domains.NodeType;
import org.springframework.stereotype.Component;

@Component
public class CameoHelper {

    public static NodeType getNodeType(ElementJson e) {
        return NodeType.PACKAGE;
    }

}
