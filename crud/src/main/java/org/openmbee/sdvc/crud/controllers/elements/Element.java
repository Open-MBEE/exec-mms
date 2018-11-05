package org.openmbee.sdvc.crud.controllers.elements;

import java.time.Instant;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openmbee.sdvc.crud.config.DbContextHolder;
import org.openmbee.sdvc.crud.domains.Node;
import org.springframework.stereotype.Service;

@Service
public class Element {

    protected final Logger logger = LogManager.getLogger(getClass());

    private String id;
    private String documentation;
    private String ownerId;
    private String type;
    private boolean isDerived;
    private Object value;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDocumentation() {
        return documentation;
    }

    public void setDocumentation(String documentation) {
        this.documentation = documentation;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isDerived() {
        return isDerived;
    }

    public void setDerived(boolean derived) {
        isDerived = derived;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public Node toNode() {
        logger.error("Context: project: {}, ref: {}", DbContextHolder.getContext().getProjectId(),
            DbContextHolder.getContext().getBranchId());

        Node node = new Node();
        node.setSysmlId(this.id);
        node.setElasticId("someElasticId");
        node.setLastCommit("someOtherElasticId");
        node.setInitialCommit("yetAnotherElasticId");
        node.setDeleted(false);
        node.setCreated(Instant.now());
        node.setCreatedBy("Someone");
        node.setModified(Instant.now());
        node.setModifiedBy("Someone else");

        return node;
    }
}
