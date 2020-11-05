package org.openmbee.mms.cameo.services;

import org.openmbee.mms.data.domains.scoped.Node;
import org.openmbee.mms.json.ElementJson;

public class PropertyData {

    private Node propertyNode;

    private Node assocNode;

    private Node assocPropertyNode;

    private ElementJson typeJson;

    private ElementJson propertyJson;

    private ElementJson assocJson;

    private ElementJson assocPropertyJson;

    private boolean isView;

    public Node getPropertyNode() {
        return propertyNode;
    }

    public void setPropertyNode(Node propertyNode) {
        this.propertyNode = propertyNode;
    }

    public ElementJson getTypeJson() {
        return typeJson;
    }

    public void setTypeJson(ElementJson typeJson) {
        this.typeJson = typeJson;
    }

    public ElementJson getPropertyJson() {
        return propertyJson;
    }

    public void setPropertyJson(ElementJson propertyJson) {
        this.propertyJson = propertyJson;
    }

    public boolean isView() {
        return isView;
    }

    public void setView(boolean view) {
        isView = view;
    }

    public Node getAssocNode() {
        return assocNode;
    }

    public void setAssocNode(Node assocNode) {
        this.assocNode = assocNode;
    }

    public ElementJson getAssocJson() {
        return assocJson;
    }

    public void setAssocJson(ElementJson assocJson) {
        this.assocJson = assocJson;
    }

    public Node getAssocPropertyNode() {
        return assocPropertyNode;
    }

    public void setAssocPropertyNode(Node assocPropertyNode) {
        this.assocPropertyNode = assocPropertyNode;
    }

    public ElementJson getAssocPropertyJson() {
        return assocPropertyJson;
    }

    public void setAssocPropertyJson(ElementJson assocPropertyJson) {
        this.assocPropertyJson = assocPropertyJson;
    }
}
