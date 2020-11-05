package org.openmbee.mms.core.services;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.openmbee.mms.core.objects.Rejection;
import org.openmbee.mms.data.domains.scoped.Node;
import org.openmbee.mms.json.ElementJson;

public class NodeGetInfo {

    Set<String> reqIndexIds;

    Map<String, ElementJson> reqElementMap;

    Map<String, Node> existingNodeMap;

    // string is node id
    // map of element json objects
    Map<String, ElementJson> existingElementMap;

    Map<String, ElementJson> activeElementMap;

    Map<String, Rejection> rejected;


    public Set<String> getReqIndexIds() {
        return reqIndexIds;
    }

    public NodeGetInfo setReqIndexIds(Set<String> reqIndexIds) {
        this.reqIndexIds = reqIndexIds;
        return this;
    }

    public Map<String, ElementJson> getReqElementMap() {
        return reqElementMap;
    }

    public NodeGetInfo setReqElementMap(Map<String, ElementJson> reqElementMap) {
        this.reqElementMap = reqElementMap;
        return this;
    }

    public Map<String, Node> getExistingNodeMap() {
        return existingNodeMap;
    }

    public NodeGetInfo setExistingNodeMap(Map<String, Node> existingNodeMap) {
        this.existingNodeMap = existingNodeMap;
        return this;
    }

    public Map<String, ElementJson> getActiveElementMap() {
        return activeElementMap;
    }

    public NodeGetInfo setActiveElementMap(
        Map<String, ElementJson> activeElementMap) {
        this.activeElementMap = activeElementMap;
        return this;
    }

    public Map<String, ElementJson> getExistingElementMap() {
        return existingElementMap;
    }

    public NodeGetInfo setExistingElementMap(
        Map<String, ElementJson> existingElementMap) {
        this.existingElementMap = existingElementMap;
        return this;
    }

    public Map<String, Rejection> getRejected() {
        return rejected;
    }

    public NodeGetInfo setRejected(Map<String, Rejection> rejected) {
        this.rejected = rejected;
        return this;
    }

    public void addRejection(String id, Rejection rejection) {
        if (this.rejected == null) {
            this.rejected = new HashMap<>();
        }
        this.rejected.put(id, rejection);
    }

}
