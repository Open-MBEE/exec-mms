package org.openmbee.sdvc.cameo.services;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.openmbee.sdvc.cameo.CameoConstants;
import org.openmbee.sdvc.json.ElementJson;
import org.openmbee.sdvc.cameo.CameoNodeType;
import org.springframework.stereotype.Component;

@Component
public class CameoHelper {

    public CameoNodeType getNodeType(ElementJson e) {
        if (isDocument(e)) {
            return CameoNodeType.DOCUMENT;
        }
        if (isView(e)) {
            return CameoNodeType.VIEW;
        }
        if (isGroup(e)) {
            return CameoNodeType.GROUP;
        }
        if (e.getId().contains("holding_bin") || e.getId().contains("view_instances_bin")) {
            return CameoNodeType.HOLDINGBIN;
        }
        String type = e.getType();
        if (type == null) {
            return CameoNodeType.ELEMENT;
        }
        switch (type) {
            case "Mount":
                return CameoNodeType.PROJECTUSAGE;
            case "InstanceSpecification":
                return CameoNodeType.INSTANCESPECIFICATION;
            case "Constraint":
                return CameoNodeType.CONSTRAINT;
            case "Package":
                return CameoNodeType.PACKAGE;
            case "Property":
                return CameoNodeType.PROPERTY;
            case "Parameter":
                return CameoNodeType.PARAMETER;
            default:
        }
        return CameoNodeType.ELEMENT;
    }

    public boolean isView(ElementJson e) {
        List<String> sids = (List)e.get(CameoConstants.APPLIEDSTEREOTYPEIDS);
        if (sids != null && !sids.isEmpty()) {
            Set<String> ids = new HashSet<>(sids);
            ids.retainAll(CameoConstants.VIEWSIDS);
            return !ids.isEmpty();
        }
        return false;
    }

    public boolean isDocument(ElementJson e) {
        List<String> sids = (List)e.get(CameoConstants.APPLIEDSTEREOTYPEIDS);
        if (sids != null && sids.contains(CameoConstants.DOCUMENTSID)) {
            return true;
        }
        return false;
    }

    public boolean isGroup(ElementJson e) {
        Boolean isGroup = (Boolean)e.get(CameoConstants.ISGROUP);
        if (isGroup != null) {
            return isGroup;
        }
        return false;
    }
}
