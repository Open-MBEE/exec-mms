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
import org.openmbee.sdvc.crud.repositories.commit.CommitDAO;
import org.openmbee.sdvc.data.domains.Edge;
import org.openmbee.sdvc.data.domains.Node;
import org.openmbee.sdvc.crud.repositories.node.NodeDAO;
import org.openmbee.sdvc.crud.repositories.node.NodeIndexDAO;
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

    public void initCommitJson(CommitJson cmjs, Instant now) {
        cmjs.setId(UUID.randomUUID().toString());
        cmjs.setIndexId(cmjs.getId());
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
            indexIds.add(node.getIndexId());
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
        info.setOldIndexIds(new HashSet<>());
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
        newObj.put(BaseJson.INDEXID, e.getIndexId());
        newObj.put(BaseJson.ID, e.getId());
        cmjs.getAdded().add(newObj);

        n.setNodeId(e.getId());
        n.setIndexId(e.getIndexId());
        n.setLastCommit(cmjs.getId());
        n.setInitialCommit(e.getIndexId());
        n.setNodeType(0);
        n.setDeleted(false);
    }

    public void processElementUpdated(ElementJson e, Node n, CommitJson cmjs) {
        processElementAddedOrUpdated(e, n, cmjs);

        Map<String, Object> newObj = new HashMap<>();
        newObj.put(CommitJson.PREVIOUS, n.getIndexId());
        newObj.put(CommitJson.TYPE, "Element");
        newObj.put(BaseJson.INDEXID, e.getIndexId());
        newObj.put(BaseJson.ID, e.getId());
        cmjs.getUpdated().add(newObj);

        n.setIndexId(e.getIndexId());
        n.setLastCommit(cmjs.getId());
        n.setNodeType(0);
        n.setDeleted(false);
    }

    public void processElementAddedOrUpdated(ElementJson e, Node n, CommitJson cmjs) {
        e.setProjectId(cmjs.getProjectId());
        e.setRefId(cmjs.getRefId());
        List<String> inRefIds = new ArrayList<>();
        inRefIds.add(cmjs.getRefId());
        e.setInRefIds(inRefIds);
        String elasticId = UUID.randomUUID().toString();
        e.setIndexId(elasticId);
        e.setCommitId(cmjs.getId());
        e.setModified(cmjs.getCreated());
        e.setModifier(cmjs.getCreator());
    }

    public void processElementDeleted(ElementJson e, Node n, CommitJson cmjs) {
        Map<String, Object> newObj = new HashMap<>();
        newObj.put(CommitJson.PREVIOUS, n.getIndexId());
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
                //TODO duplicate relationships
            }
        }
        return res;
    }

    public boolean existingNodeContainsNodeId(NodeGetInfo info, String nodeId) {
        if (!info.getExistingNodeMap().containsKey(nodeId)) {
            Map<String, Object> reject = new HashMap<>();
            reject.put("code", 404);
            reject.put("message", "not found");
            reject.put("id", nodeId);
            info.getRejected().add(reject);
            return false;
        }
        return true;
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
