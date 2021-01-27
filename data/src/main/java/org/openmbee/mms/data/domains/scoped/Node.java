package org.openmbee.mms.data.domains.scoped;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "\"nodes\"")
public class Node {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    Long id;

    @Column(unique = true, length = 512)
    private String nodeId;
    private String docId;
    private String lastCommit;
    private String initialCommit;
    private boolean deleted;

    @Column(columnDefinition = "smallint")
    private Integer nodeType;

    public Node() {
    }

    public Node(long id, String nodeId, String docId, String lastCommit, String initialCommit,
        boolean deleted, Integer nodeType) {
        setId(id);
        setNodeId(nodeId);
        setDocId(docId);
        setLastCommit(lastCommit);
        setInitialCommit(initialCommit);
        setDeleted(deleted);
        setNodeType(nodeType);
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    public String getDocId() {
        return docId;
    }

    public void setDocId(String docId) {
        this.docId = docId;
    }

    public String getLastCommit() {
        return lastCommit;
    }

    public void setLastCommit(String lastCommit) {
        this.lastCommit = lastCommit;
    }

    public String getInitialCommit() {
        return initialCommit;
    }

    public void setInitialCommit(String initialCommit) {
        this.initialCommit = initialCommit;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public Integer getNodeType() {
        return nodeType;
    }

    public void setNodeType(Integer nodeType) {
        this.nodeType = nodeType;
    }

}
