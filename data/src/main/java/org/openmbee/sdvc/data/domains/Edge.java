package org.openmbee.sdvc.data.domains;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "edges")
public class Edge {

    public Edge() {
    }

    public Edge(long id, long parent, long child, int edgeType) {
        setId(id);
        setParent(parent);
        setChild(child);
        setEdgeType(edgeType);
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    Long id;

    private Long parent;

    private Long child;

    @Column(columnDefinition = "smallint")
    private Integer edgeType;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getParent() {
        return parent;
    }

    public void setParent(Long parent) {
        this.parent = parent;
    }

    public Long getChild() {
        return child;
    }

    public void setChild(Long child) {
        this.child = child;
    }

    public Integer getEdgeType() {
        return edgeType;
    }

    public void setEdgeType(Integer edgeType) {
        this.edgeType = edgeType;
    }
}
