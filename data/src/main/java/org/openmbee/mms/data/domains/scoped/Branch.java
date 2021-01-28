package org.openmbee.mms.data.domains.scoped;

import java.time.Instant;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "branches")
public class Branch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    Long id;

    private String description;

    @Column(unique = true, length = 512)
    private String branchId;
    private String docId;
    @Column(length = 512)
    private String branchName;

    @Column(length = 512)
    private String parentRefId;
    private Long parentCommit;

    private Instant timestamp;

    private boolean tag;
    private boolean deleted;

    public Branch() {

    }

    public Branch(String docId, String description, String branchId, String branchName, String parentRef,
        Long parentCommit, Instant timestamp, boolean tag, boolean deleted) {
        setDocId(docId);
        setDescription(description);
        setBranchId(branchId);
        setBranchName(branchName);
        setParentRefId(parentRef);
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public String getDocId() {
        return docId;
    }

    public void setDocId(String docId) {
        this.docId = docId;
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

    public Long getParentCommit() {
        return parentCommit;
    }

    public void setParentCommit(Long parentCommit) {
        this.parentCommit = parentCommit;
    }
}
