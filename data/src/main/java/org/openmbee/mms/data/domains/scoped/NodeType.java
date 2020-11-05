package org.openmbee.mms.data.domains.scoped;

import javax.persistence.Table;

@Table(name = "nodetypes")
public enum NodeType {

    ELEMENT(1);

    private int id;

    NodeType(int id) {
        this.id = id;
    }

    public static NodeType getNodeType(int id) {
        for (NodeType nodeType : values()) {
            if (nodeType.getId() == id) {
                return nodeType;
            }
        }
        return null;
    }

    public int getId() {
        return id;
    }
}
