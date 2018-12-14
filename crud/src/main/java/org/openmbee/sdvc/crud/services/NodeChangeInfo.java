package org.openmbee.sdvc.crud.services;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.openmbee.sdvc.crud.domains.Node;
import org.openmbee.sdvc.json.CommitJson;
import org.openmbee.sdvc.json.ElementJson;
import org.springframework.data.util.Pair;

public class NodeChangeInfo {

    Set<String> reqIndexIds;

    Map<String, ElementJson> reqElementMap;

    Map<String, Node> existingNodeMap;

    Map<String, Map<String, Object>> existingElementMap;

    Map<String, Node> toSaveNodeMap;

    Map<String, ElementJson> updatedMap;

    Map<String, ElementJson> deletedMap;

    Set<String> oldIndexIds;

    CommitJson commitJson;

    List<Map> rejected;

    Instant now;

    Map<Integer, List<Pair<String, String>>> edgesToDelete;

    Map<Integer, List<Pair<String, String>>> edgesToSave;

    public Map<Integer, List<Pair<String, String>>> getEdgesToDelete() {
        return edgesToDelete;
    }

    public NodeChangeInfo setEdgesToDelete(
        Map<Integer, List<Pair<String, String>>> edgesToDelete) {
        this.edgesToDelete = edgesToDelete;
        return this;
    }

    public Map<Integer, List<Pair<String, String>>> getEdgesToSave() {
        return edgesToSave;
    }

    public NodeChangeInfo setEdgesToSave(
        Map<Integer, List<Pair<String, String>>> edgesToSave) {
        this.edgesToSave = edgesToSave;
        return this;
    }

    public Instant getNow() {
        return now;
    }

    public void setNow(Instant now) {
        this.now = now;
    }

    public Set<String> getReqIndexIds() {
        return reqIndexIds;
    }

    public NodeChangeInfo setReqIndexIds(Set<String> reqIndexIds) {
        this.reqIndexIds = reqIndexIds;
        return this;
    }

    public Map<String, ElementJson> getReqElementMap() {
        return reqElementMap;
    }

    public NodeChangeInfo setReqElementMap(Map<String, ElementJson> reqElementMap) {
        this.reqElementMap = reqElementMap;
        return this;
    }

    public Map<String, Node> getExistingNodeMap() {
        return existingNodeMap;
    }

    public NodeChangeInfo setExistingNodeMap(Map<String, Node> existingNodeMap) {
        this.existingNodeMap = existingNodeMap;
        return this;
    }

    public Map<String, Map<String, Object>> getExistingElementMap() {
        return existingElementMap;
    }

    public NodeChangeInfo setExistingElementMap(
        Map<String, Map<String, Object>> existingElementMap) {
        this.existingElementMap = existingElementMap;
        return this;
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

    public Set<String> getOldIndexIds() {
        return oldIndexIds;
    }

    public NodeChangeInfo setOldIndexIds(Set<String> oldIndexIds) {
        this.oldIndexIds = oldIndexIds;
        return this;
    }

    public CommitJson getCommitJson() {
        return commitJson;
    }

    public NodeChangeInfo setCommitJson(CommitJson commitJson) {
        this.commitJson = commitJson;
        return this;
    }

    public List<Map> getRejected() {
        return rejected;
    }

    public NodeChangeInfo setRejected(List<Map> rejected) {
        this.rejected = rejected;
        return this;
    }


}
