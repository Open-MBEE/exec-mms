package org.openmbee.sdvc.crud.services;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openmbee.sdvc.core.objects.Rejection;
import org.openmbee.sdvc.core.services.NodeChangeInfo;
import org.openmbee.sdvc.core.services.NodeGetInfo;
import org.openmbee.sdvc.core.dao.BranchDAO;
import org.openmbee.sdvc.core.dao.CommitDAO;
import org.openmbee.sdvc.data.domains.scoped.Node;
import org.openmbee.sdvc.core.dao.NodeDAO;
import org.openmbee.sdvc.core.dao.NodeIndexDAO;
import org.openmbee.sdvc.json.CommitJson;
import org.openmbee.sdvc.json.CommitUpdatedJson;
import org.openmbee.sdvc.json.ElementJson;
import org.springframework.beans.factory.annotation.Autowired;
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
        info.setRejected(new HashMap<>());
        info.setNow(now);
        info.setOldDocIds(new HashSet<>());
        info.setEdgesToDelete(new HashMap<>());
        info.setEdgesToSave(new HashMap<>());
        info.setActiveElementMap(new HashMap<>());
        return info;
    }

    public void processElementAdded(ElementJson e, Node n, NodeChangeInfo info) {
        CommitJson cmjs = info.getCommitJson();
        processElementAddedOrUpdated(e, n, info);

        e.setCreator(cmjs.getCreator()); //Only set on creation of new element
        e.setCreated(cmjs.getCreated());

        CommitUpdatedJson newObj = new CommitUpdatedJson()
            .setDocId(e.getDocId())
            .setId(e.getId())
            .setType("Element");
        cmjs.getAdded().add(newObj);

        n.setNodeId(e.getId());
        n.setInitialCommit(e.getDocId());
    }

    public void processElementUpdated(ElementJson e, Node n, NodeChangeInfo info) {
        String previousDocId = n.getDocId();
        processElementAddedOrUpdated(e, n, info);

        info.getOldDocIds().add(previousDocId);
        CommitUpdatedJson newObj= new CommitUpdatedJson()
            .setPreviousDocId(previousDocId)
            .setDocId(e.getDocId())
            .setId(e.getId())
            .setType("Element");
        info.getCommitJson().getUpdated().add(newObj);
    }

    private void processElementAddedOrUpdated(ElementJson e, Node n, NodeChangeInfo info) {
        CommitJson cmjs = info.getCommitJson();
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

        info.getToSaveNodeMap().put(e.getId(), n);
        info.getUpdatedMap().put(e.getId(), e);
    }

    public void processElementDeleted(ElementJson e, Node n, NodeChangeInfo info) {
        CommitUpdatedJson newObj = new CommitUpdatedJson()
            .setPreviousDocId(n.getDocId())
            .setId(e.getId())
            .setType("Element");
        info.getCommitJson().getDeleted().add(newObj);
        info.getOldDocIds().add(n.getDocId());
        info.getToSaveNodeMap().put(n.getNodeId(), n);
        n.setDeleted(true);
    }

    public boolean existingNodeContainsNodeId(NodeGetInfo info, String nodeId) {
        if (!info.getExistingNodeMap().containsKey(nodeId)) {
            rejectNotFound(info, nodeId);
            return false;
        }
        return true;
    }

    protected void rejectNotFound(NodeGetInfo info, String nodeId) {
        info.addRejection(nodeId, new Rejection(nodeId, 404, "Not Found"));
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

    public static List<ElementJson> sort(List<String> ids, List<ElementJson> orig) {
        Map<String, ElementJson> map = convertJsonToMap(orig);
        List<ElementJson> ret = new ArrayList<>();
        for (String id: ids) {
            if (map.containsKey(id)) {
                ret.add(map.get(id));
            }
        }
        return ret;
    }

    //find first element of type in types following e's relkey (assuming relkey's value is an element id)
    public Optional<ElementJson> getFirstRelationshipOfType(ElementJson e, List<Integer> types, String relkey) {
        //TODO to use some graph interface sometime
        //only for latest graph
        String nextId = (String)e.get(relkey);
        if (nextId == null || nextId.isEmpty()) {
            return Optional.empty();
        }
        Optional<Node> nextNode = nodeRepository.findByNodeId(nextId);
        while (nextNode.isPresent() && !nextNode.get().isDeleted()) {
            Optional<ElementJson> nextJson = nodeIndex.findById(nextNode.get().getDocId());
            if (!nextJson.isPresent()) {
                return Optional.empty();
            }
            if (types.contains(nextNode.get().getNodeType())) {
                return nextJson;
            }
            nextId = (String)nextJson.get().get(relkey);
            if (nextId == null || nextId.isEmpty()) {
                return Optional.empty();
            }
            nextNode = nodeRepository.findByNodeId(nextId);
        }
        return Optional.empty();
    }
}
