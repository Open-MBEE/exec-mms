package org.openmbee.sdvc.data.domains;

import javax.persistence.Table;

@Table(name = "edgetypes")
public enum EdgeType {
    ;

    private int id;

    EdgeType(int id) {
        this.id = id;
    }

    public static EdgeType getFromValue(int id) {
        for (EdgeType edgeType : EdgeType.values()) {
            if (edgeType.getId() == id) {
                return edgeType;
            }
        }

        return null;
    }

    public int getId() {
        return id;
    }
}
