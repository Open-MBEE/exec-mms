package org.openmbee.sdvc.cameo.services;

import org.openmbee.sdvc.json.ElementJson;
import org.openmbee.sdvc.cameo.CameoNodeType;
import org.springframework.stereotype.Component;

@Component
public class CameoHelper {

    public static CameoNodeType getNodeType(ElementJson e) {
        return CameoNodeType.PACKAGE;
    }

}
