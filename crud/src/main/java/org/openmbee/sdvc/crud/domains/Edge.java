package org.openmbee.sdvc.crud.domains;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import org.openmbee.sdvc.core.domains.Base;

@Entity
@Table(name = "edges")
public class Edge extends Base {

    @ManyToOne(fetch = FetchType.LAZY,
        cascade =  CascadeType.ALL)
    private Node parent;

    @ManyToOne(fetch = FetchType.LAZY,
        cascade =  CascadeType.ALL)
    private Node child;

    @Column(columnDefinition = "smallint")
    private EdgeType edgeType;

    public Node getParent() {
        return parent;
    }

    public void setParent(Node parent) {
        this.parent = parent;
    }

    public Node getChild() {
        return child;
    }

    public void setChild(Node child) {
        this.child = child;
    }

    public EdgeType getEdgeType() {
        return edgeType;
    }

    public void setEdgeType(EdgeType edgeType) {
        this.edgeType = edgeType;
    }
}
