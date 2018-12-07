package org.openmbee.sdvc.crud.controllers.elements;

import org.openmbee.sdvc.crud.controllers.BaseJson;
import org.openmbee.sdvc.crud.domains.Node;
import org.openmbee.sdvc.crud.domains.NodeType;

public class ElementJson extends BaseJson {

    public Node toNode() {
        Node n = new Node();
        n.setSysmlId(getId());
        n.setElasticId(getElasticId());
        n.setNodeType(NodeType.getNodeType(1));
        return n;
    }
}
