package org.openmbee.sdvc.crud.controllers.elements;

import org.openmbee.sdvc.crud.controllers.BaseJson;
import org.openmbee.sdvc.crud.domains.Node;

public class ElementJson extends BaseJson {
    public Node toNode() {
        Node n = new Node();
        n.setSysmlId(getId());
        return n;
    }
}
