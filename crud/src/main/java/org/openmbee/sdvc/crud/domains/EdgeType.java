package org.openmbee.sdvc.crud.domains;

import javax.persistence.Table;

@Table(name = "edgetypes")
public enum EdgeType {
    CONTAINMENT(1),
    VIEW(2),
    TRANSCLUSION(3),
    CHILDVIEW(4);

    private int id;

    EdgeType(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
