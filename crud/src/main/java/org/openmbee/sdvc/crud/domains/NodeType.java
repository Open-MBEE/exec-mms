package org.openmbee.sdvc.crud.domains;

import javax.persistence.Table;

@Table(name = "nodetypes")
public enum NodeType {

    ELEMENT(1),
    SITE(2),
    PROJECT(3),
    DOCUMENT(4),
    COMMENT(5),
    CONSTRAINT(6),
    INSTANCESPECIFICATION(7),
    OPERATION(8),
    PACKAGE(9),
    PROPERTY(10),
    PARAMETER(11),
    VIEW(12),
    VIEWPOINT(13),
    SITEANDPACKAGE(14),
    HOLDINGBIN(15),
    MOUNT(16);

    private int id;

    NodeType(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public static NodeType getNodeType(int id) {
        for (NodeType nodeType : values()) {
            if (nodeType.getId() == id) {
                return nodeType;
            }
        }
        return null;
    }
}
