package org.openmbee.sdvc.crud.services;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openmbee.sdvc.core.objects.Rejection;
import org.openmbee.sdvc.core.services.NodeChangeInfo;
import org.openmbee.sdvc.core.services.NodeGetInfo;
import org.openmbee.sdvc.core.dao.BranchDAO;
import org.openmbee.sdvc.core.dao.CommitDAO;
import org.openmbee.sdvc.data.domains.scoped.Edge;
import org.openmbee.sdvc.data.domains.scoped.Node;
import org.openmbee.sdvc.core.dao.NodeDAO;
import org.openmbee.sdvc.core.dao.NodeIndexDAO;
import org.openmbee.sdvc.json.BaseJson;
import org.openmbee.sdvc.json.CommitJson;
import org.openmbee.sdvc.json.ElementJson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

@Service
public class NodeOperation {

    protected final Logger logger = LogManager.getLogger(getClass());
    protected NodeDAO nodeRepository;
    protected NodeIndexDAO nodeIndex;
    protected CommitDAO commitRepository;
    protected BranchDAO branchRepository;

    protected DateTimeFormatter formatter = DateTimeFormatter
        .ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ").withZone(
            ZoneId.systemDefault());

    @Autowired
    public void setNodeRepository(NodeDAO nodeRepository) {
        this.nodeRepository = nodeRepository;
    }

    @Autowired
    public void setNodeIndex(NodeIndexDAO nodeIndex) {
        this.nodeIndex = nodeIndex;
    }

    @Autowired
    public void setCommitRepository(CommitDAO commitRepository) {
        this.commitRepository = commitRepository;
    }

    @Autowired
    public void setBranchRepository(BranchDAO branchRepository) {
        this.branchRepository = branchRepository;
    }

    public void initCommitJson(CommitJson cmjs, Instant now) {
        cmjs.setId(UUID.randomUUID().toString());
        cmjs.setDocId(cmjs.getId());
        cmjs.setCreated(formatter.format(now));
        cmjs.setAdded(new ArrayList<>());
        cmjs.setDeleted(new ArrayList<>());
        cmjs.setUpdated(new ArrayList<>());
    }

    public NodeChangeInfo initInfo(List<ElementJson> elements, CommitJson cmjs) {

        Set<String> indexIds = new HashSet<>();
        Map<String, ElementJson> reqElementMap = convertJsonToMap(elements);
        List<Node> existingNodes = nodeRepository.findAllByNodeIds(reqElementMap.keySet());
        Map<String, Node> existingNodeMap = new HashMap<>();
        for (Node node : existingNodes) {
            indexIds.add(node.getDocId());
            existingNodeMap.put(node.getNodeId(), node);
        }
        // bulk read existing elements in elastic
        List<ElementJson> existingElements = nodeIndex.findAllById(indexIds);
        Map<String, ElementJson> existingElementMap = convertJsonToMap(existingElements);

        Instant now = Instant.now();
        if (cmjs != null) {
            initCommitJson(cmjs, now);
        }

        NodeChangeInfo info = new NodeChangeInfo();
        info.setCommitJson(cmjs);
        info.setUpdatedMap(new HashMap<>());
        info.setDeletedMap(new HashMap<>());
        info.setExistingElementMap(existingElementMap);
        info.setExistingNodeMap(existingNodeMap);
        info.setReqElementMap(reqElementMap);
        info.setReqIndexIds(indexIds);
        info.setToSaveNodeMap(new HashMap<>());
        info.setRejected(new ArrayList<>());
        info.setNow(now);
        info.setOldDocIds(new HashSet<>());
        info.setEdgesToDelete(new HashMap<>());
        info.setEdgesToSave(new HashMap<>());
        info.setActiveElementMap(new HashMap<>());
        return info;
    }

    public void processElementAdded(ElementJson e, Node n, CommitJson cmjs) {
        processElementAddedOrUpdated(e, n, cmjs);

        e.setCreator(cmjs.getCreator()); //Only set on creation of new element
        e.setCreated(cmjs.getCreated());

        Map<String, Object> newObj = new HashMap<>();
        newObj.put(CommitJson.TYPE, "Element");
        newObj.put(BaseJson.DOCID, e.getDocId());
        newObj.put(BaseJson.ID, e.getId());
        cmjs.getAdded().add(newObj);

        n.setNodeId(e.getId());
        n.setInitialCommit(e.getDocId());
    }

    public void processElementUpdated(ElementJson e, Node n, CommitJson cmjs) {
        processElementAddedOrUpdated(e, n, cmjs);

        Map<String, Object> newObj = new HashMap<>();
        newObj.put(CommitJson.PREVIOUS, n.getDocId());
        newObj.put(CommitJson.TYPE, "Element");
        newObj.put(BaseJson.DOCID, e.getDocId());
        newObj.put(BaseJson.ID, e.getId());
        cmjs.getUpdated().add(newObj);
    }

    public void processElementAddedOrUpdated(ElementJson e, Node n, CommitJson cmjs) {
        e.setProjectId(cmjs.getProjectId());
        e.setRefId(cmjs.getRefId());
        List<String> inRefIds = new ArrayList<>();
        inRefIds.add(cmjs.getRefId());
        e.setInRefIds(inRefIds);
        String elasticId = UUID.randomUUID().toString();
        e.setDocId(elasticId);
        e.setCommitId(cmjs.getId());
        e.setModified(cmjs.getCreated());
        e.setModifier(cmjs.getCreator());

        n.setDocId(e.getDocId());
        n.setLastCommit(cmjs.getId());
        n.setDeleted(false);
        n.setNodeType(0);
    }

    public void processElementDeleted(ElementJson e, Node n, CommitJson cmjs) {
        Map<String, Object> newObj = new HashMap<>();
        newObj.put(CommitJson.PREVIOUS, n.getDocId());
        newObj.put(CommitJson.TYPE, "Element");
        newObj.put(BaseJson.ID, e.getId());
        cmjs.getDeleted().add(newObj);

        n.setDeleted(true);
    }

    public List<Edge> getEdgesToSave(NodeChangeInfo info) {
        Set<String> toFind = new HashSet<>();
        Map<String, Node> nodes = info.getToSaveNodeMap();
        List<Edge> res = new ArrayList<>();
        Map<Integer, List<Pair<String, String>>> edges = info.getEdgesToSave();
        if (edges.isEmpty()) {
            return res;
        }
        for (Map.Entry<Integer, List<Pair<String, String>>> entry : edges.entrySet()) {
            for (Pair<String, String> pair : entry.getValue()) {
                toFind.add(pair.getFirst());
                toFind.add(pair.getSecond());
            }
        }
        toFind.removeAll(nodes.keySet());
        Map<String, Node> edgeNodes = convertNodesToMap(nodeRepository.findAllByNodeIds(toFind));
        edgeNodes.putAll(nodes);

        for (Map.Entry<Integer, List<Pair<String, String>>> entry : edges.entrySet()) {
            for (Pair<String, String> pair : entry.getValue()) {
                Node parent = edgeNodes.get(pair.getFirst());
                Node child = edgeNodes.get(pair.getSecond());
                if (parent == null || child == null) {
                    continue; //TODO error or specific remedy?
                }
                Edge e = new Edge();
                e.setParent(parent.getId());
                e.setChild(child.getId());
                e.setEdgeType(entry.getKey());
                res.add(e); //TODO there's currently no unique constraint on parent child pair,
            }
        }
        return res;
    }

    public boolean existingNodeContainsNodeId(NodeGetInfo info, String nodeId) {
        if (!info.getExistingNodeMap().containsKey(nodeId)) {
            rejectNotFound(info, nodeId);
            return false;
        }
        return true;
    }

    protected void rejectNotFound(NodeGetInfo info, String nodeId) {
        info.addRejection(new Rejection(nodeId, 404, "Not Found"));
    }

    public static Map<String, ElementJson> convertJsonToMap(
        List<ElementJson> elements) {
        Map<String, ElementJson> result = new HashMap<>();
        for (ElementJson elem : elements) {
            if (elem == null) {
                continue;
            }
            if (elem.getId() != null && !elem.getId().equals("")) {
                result.put(elem.getId(), elem);
            }
        }
        return result;
    }

    public static Map<String, Node> convertNodesToMap(List<Node> nodes) {
        Map<String, Node> result = new HashMap<>();
        for (Node node : nodes) {
            if (node == null) {
                continue;
            }
            if (node.getNodeId() != null && !node.getNodeId().equals("")) {
                result.put(node.getNodeId(), node);
            }
        }
        return result;
    }
}
