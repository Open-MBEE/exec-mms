package org.openmbee.mms.core.services;

import java.time.Instant;
import java.util.Map;
import java.util.Set;
import org.openmbee.mms.data.domains.scoped.Node;
import org.openmbee.mms.json.CommitJson;
import org.openmbee.mms.json.ElementJson;

public class NodeChangeInfo extends NodeGetInfo {

    Map<String, Node> toSaveNodeMap;

    Map<String, ElementJson> updatedMap;

    Map<String, ElementJson> deletedMap;

    Set<String> oldDocIds;

    CommitJson commitJson;

    Instant now;

    public Instant getNow() {
        return now;
    }

    public void setNow(Instant now) {
        this.now = now;
    }

    public Map<String, Node> getToSaveNodeMap() {
        return toSaveNodeMap;
    }

    public NodeChangeInfo setToSaveNodeMap(Map<String, Node> toSaveNodeMap) {
        this.toSaveNodeMap = toSaveNodeMap;
        return this;
    }

    public Map<String, ElementJson> getUpdatedMap() {
        return updatedMap;
    }

    public NodeChangeInfo setUpdatedMap(Map<String, ElementJson> updatedMap) {
        this.updatedMap = updatedMap;
        return this;
    }

    public Map<String, ElementJson> getDeletedMap() {
        return deletedMap;
    }

    public NodeChangeInfo setDeletedMap(Map<String, ElementJson> deletedMap) {
        this.deletedMap = deletedMap;
        return this;
    }

    public Set<String> getOldDocIds() {
        return oldDocIds;
    }

    public NodeChangeInfo setOldDocIds(Set<String> oldDocIds) {
        this.oldDocIds = oldDocIds;
        return this;
    }

    public CommitJson getCommitJson() {
        return commitJson;
    }

    public NodeChangeInfo setCommitJson(CommitJson commitJson) {
        this.commitJson = commitJson;
        return this;
    }
}
