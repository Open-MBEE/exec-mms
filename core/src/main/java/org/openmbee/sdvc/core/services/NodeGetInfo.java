package org.openmbee.sdvc.core.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.openmbee.sdvc.core.objects.Rejection;
import org.openmbee.sdvc.data.domains.scoped.Node;
import org.openmbee.sdvc.json.ElementJson;

public class NodeGetInfo {

    Set<String> reqIndexIds;

    Map<String, ElementJson> reqElementMap;

    Map<String, Node> existingNodeMap;

    // string is node id
    // map of element json objects
    Map<String, ElementJson> existingElementMap;

    Map<String, ElementJson> activeElementMap;

    List<Rejection> rejected;


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

    public List<Rejection> getRejected() {
        return rejected;
    }

    public NodeGetInfo setRejected(List<Rejection> rejected) {
        this.rejected = rejected;
        return this;
    }

    public void addRejection(Rejection rejection) {
        if (this.rejected == null) {
            this.rejected = new ArrayList<>();
        }
        this.rejected.add(rejection);
    }

}
