package org.openmbee.sdvc.crud.domains;

import java.time.Instant;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import org.openmbee.sdvc.core.domains.Base;

@Entity
@Table(name = "nodes")
public class Node extends Base {

    private String sysmlId;
    private String elasticId;
    private String lastCommit;
    private String initialCommit;
    private String createdBy;
    private String modifiedBy;
    private boolean deleted;

    @Column(columnDefinition = "smallint")
    private NodeType nodeType;

    public Node() {
    }

    public Node(long id, String sysmlId, String elasticId, String lastCommit, String initialCommit,
        boolean deleted, Instant created, String createdBy, Instant modified, String modifiedBy) {
        setId(id);
        setSysmlId(sysmlId);
        setElasticId(elasticId);
        setLastCommit(lastCommit);
        setInitialCommit(initialCommit);
        setDeleted(deleted);
        setCreated(created);
        setCreatedBy(createdBy);
        setModified(modified);
        setModifiedBy(modifiedBy);
    }
/*
    public static Node toNode(Map<String, Object> node) {
        return new Node(
            node.getOrDefault("id", null).toString(),
            node.getOrDefault("sysmlid", null).toString(),
            node.getOrDefault("elasticid", null).toString(),
            node.getOrDefault("lastcommit", null).toString(),
            node.getOrDefault("initialcommit", null).toString(),
            (boolean) node.getOrDefault("deleted", false),
            node.getOrDefault("created", null),
            node.getOrDefault("createdby", null).toString(),
            node.getOrDefault("modified", null),
            node.getOrDefault("modifiedby", null).toString()
        );
    }
*/
    public String getSysmlId() {
        return sysmlId;
    }

    public void setSysmlId(String sysmlId) {
        this.sysmlId = sysmlId;
    }

    public String getElasticId() {
        return elasticId;
    }

    public void setElasticId(String elasticId) {
        this.elasticId = elasticId;
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

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    @Enumerated(EnumType.ORDINAL)
    public NodeType getNodeType() {
        return nodeType;
    }

    public void setNodeType(NodeType nodeType) {
        this.nodeType = nodeType;
    }

}
