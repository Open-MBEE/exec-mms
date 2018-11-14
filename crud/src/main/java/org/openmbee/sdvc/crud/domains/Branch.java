package org.openmbee.sdvc.crud.domains;

import java.time.Instant;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "branches")
public class Branch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    Long id;

    private String elasticId;
    private String branchId;
    private String branchName;

    @ManyToOne
    @JoinColumn(name = "parent")
    private Branch parentRef;

    @Transient
    private String parentRefId;

    @ManyToOne
    @JoinColumn(name = "parentCommit")
    private Commit parentCommit;

    @Transient
    private String parentCommitId;

    private Instant timestamp;

    private boolean tag;
    private boolean deleted;

    public Branch() {

    }

    public Branch(String elasticId, String branchId, String branchName, Branch parentRef,
        Commit parentCommit, Instant timestamp, boolean tag, boolean deleted) {
        setElasticId(elasticId);
        setBranchId(branchId);
        setBranchName(branchName);
        setParentRef(parentRef);
        setParentCommit(parentCommit);
        setTimestamp(timestamp);
        setTag(tag);
        setDeleted(deleted);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getElasticId() {
        return elasticId;
    }

    public void setElasticId(String elasticId) {
        this.elasticId = elasticId;
    }

    public String getBranchId() {
        return branchId;
    }

    public void setBranchId(String branchId) {
        this.branchId = branchId;
    }

    public String getBranchName() {
        return branchName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }

    public Branch getParentRef() {
        return parentRef;
    }

    public void setParentRef(Branch parentRef) {
        this.parentRef = parentRef;
    }

    public Commit getParentCommit() {
        return parentCommit;
    }

    public void setParentCommit(Commit parentCommit) {
        this.parentCommit = parentCommit;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isTag() {
        return tag;
    }

    public void setTag(boolean tag) {
        this.tag = tag;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public String getParentRefId() {
        return parentRefId;
    }

    public void setParentRefId(String parentRefId) {
        this.parentRefId = parentRefId;
    }

    public String getParentCommitId() {
        return parentCommitId;
    }

    public void setParentCommitId(String parentCommitId) {
        this.parentCommitId = parentCommitId;
    }
}
