package org.openmbee.mms.federatedpersistence.domain;

import org.openmbee.mms.core.config.Formats;
import org.openmbee.mms.core.dao.CommitPersistence;
import org.openmbee.mms.data.dao.NodeDAO;
import org.openmbee.mms.data.dao.NodeIndexDAO;
import org.openmbee.mms.core.exceptions.BadRequestException;
import org.openmbee.mms.core.exceptions.InternalErrorException;
import org.openmbee.mms.core.services.NodeGetInfo;
import org.openmbee.mms.crud.domain.NodeGetDomain;
import org.openmbee.mms.data.domains.scoped.Node;
import org.openmbee.mms.federatedpersistence.dao.FederatedNodeGetInfo;
import org.openmbee.mms.federatedpersistence.dao.FederatedNodeGetInfoImpl;
import org.openmbee.mms.json.CommitJson;
import org.openmbee.mms.json.ElementJson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

import static org.openmbee.mms.core.config.ContextHolder.getContext;

@Component
public class FederatedNodeGetDomain extends NodeGetDomain {

    private CommitPersistence commitPersistence;
    private NodeDAO nodeDAO;
    private NodeIndexDAO nodeIndex;

    @Autowired
    public FederatedNodeGetDomain(CommitPersistence commitPersistence, NodeDAO nodeDAO, NodeIndexDAO nodeIndex) {
        this.commitPersistence = commitPersistence;
        this.nodeDAO = nodeDAO;
        this.nodeIndex = nodeIndex;
    }

    public NodeGetInfo initInfo(List<ElementJson> elements, CommitJson commitJson) {
        Set<String> indexIds = new HashSet<>();
        elements.stream().map(ElementJson::getId).forEach(indexIds::add);
        List<Node> existingNodes = nodeDAO.findAllByNodeIds(indexIds);
        NodeGetInfo nodeGetInfo = initInfoFromNodes(existingNodes, commitJson);
        nodeGetInfo.getReqElementMap().putAll(convertJsonToMap(elements));
        return nodeGetInfo;
        
    }

    public NodeGetInfo initInfoFromNodes(List<Node> existingNodes, CommitJson commitJson) {
        NodeGetInfo nodeGetInfo =  super.initInfo(commitJson, this::createNodeGetInfo);
        Set<String> indexIds = new HashSet<>();
        Map<String, Node> existingNodeMap = new HashMap<>();
        for (Node node : existingNodes) {
            indexIds.add(node.getDocId());
            existingNodeMap.put(node.getNodeId(), node);
        }
        // bulk read existing elements in elastic
        List<ElementJson> existingElements = nodeIndex.findAllById(indexIds);
        addExistingElements(nodeGetInfo, existingElements); // handeled in addExistingElements
        return nodeGetInfo;
    }

    public NodeGetInfo processGetJsonFromNodes(List<Node> nodes, String commitId) {
        if (commitId == null || commitId.isEmpty()) {
            return processGetJsonFromNodes(nodes);
        }
        NodeGetInfo info = initInfoFromNodes(nodes, null);
        return processCommit(info, commitId);
    }

    public NodeGetInfo processGetJson(List<ElementJson> elements, String commitId) {
        if (commitId == null || commitId.isEmpty()) {
            return processGetJson(elements);
        }
        NodeGetInfo info = initInfo(elements, null); //gets all current nodes
        return processCommit(info, commitId);
    }


    public NodeGetInfo processGetJsonFromNodes(List<Node> nodes) {
        NodeGetInfo info = initInfoFromNodes(nodes, null);
        return processLatest(info);
    }

    public NodeGetInfo processGetJson(List<ElementJson> elements) {
        NodeGetInfo info = initInfo(elements,null);
        return processLatest(info);
    }

    protected NodeGetInfo processLatest(NodeGetInfo info) {
        if(!(info instanceof FederatedNodeGetInfo)) {
            throw new InternalErrorException("Invalid use of FederatedNodeGetDomain");
        }
        FederatedNodeGetInfo federatedInfo = (FederatedNodeGetInfo) info;
        for (String nodeId : info.getReqElementMap().keySet()) {
            if (!existingNodeContainsNodeId(federatedInfo, nodeId)) {
                continue;
            }
            ElementJson indexElement = info.getExistingElementMap().get(nodeId);

            if (federatedInfo.getExistingNodeMap().get(nodeId).isDeleted()) {
                rejectDeleted(info, nodeId, indexElement);
                continue;
            }
            info.getActiveElementMap().put(nodeId, indexElement);
        }
        return info;
    }

    protected NodeGetInfo processCommit(NodeGetInfo info, String commitId) {
        if(!(info instanceof FederatedNodeGetInfo)) {
            throw new InternalErrorException("Invalid use of FederatedNodeGetDomain");
        }
        FederatedNodeGetInfo federatedInfo = (FederatedNodeGetInfo) info;
        Optional<CommitJson> commit = commitPersistence.findById(getContext().getProjectId(), commitId);
        if (!commit.isPresent() ) {
            throw new BadRequestException("commitId is invalid");
        }
        Instant time = Instant.from(Formats.FORMATTER.parse(commit.get().getCreated())); //time of commit
        List<String> refCommitIds = null; //get it later if needed
        for (String nodeId : info.getReqElementMap().keySet()) {
            if (!existingNodeContainsNodeId(federatedInfo, nodeId)) { // nodeId not found
                continue;
            }
            ElementJson indexElement = info.getExistingElementMap().get(nodeId);
            Instant modified = Instant.from(Formats.FORMATTER.parse(indexElement.getModified()));
            Instant created = Instant.from(Formats.FORMATTER.parse(indexElement.getCreated()));

            if (commitId.equals(indexElement.getCommitId())) { //exact match
                addActiveElement(info, nodeId, indexElement);
            } else if (created.isAfter(time)) { // element created after commit
                rejectNotFound(info, nodeId);
            } else if (modified.isAfter(time)) { // latest element is after commit
                Optional<ElementJson> tryExact = nodeIndex.getByCommitId(commitId, nodeId);
                if (tryExact.isPresent()) {
                    addActiveElement(info, nodeId, tryExact.get());
                    continue; // found exact match at commit
                }
                if (refCommitIds == null) { // need list of commitIds of current ref to filter on
                    refCommitIds = getRefCommitIds(time);
                }
                Optional<ElementJson> e = nodeIndex.getElementLessThanOrEqualTimestamp(nodeId,
                    Formats.FORMATTER.format(time), refCommitIds);
                if (e.isPresent()) { // found version of element at commit time
                    //TODO determine if element was deleted at the time?
                    addActiveElement(info, nodeId, e.get());
                } else {
                    rejectNotFound(info, nodeId); // element not found at commit time
                }
            } else if (federatedInfo.getExistingNodeMap().get(nodeId).isDeleted()) { // latest element is before commit, but deleted
                rejectDeleted(info, nodeId, indexElement);
            } else { // latest element version is version at commit, not deleted
                addActiveElement(info, nodeId, indexElement);
            }
        }
        return info;
    }

    public boolean existingNodeContainsNodeId(FederatedNodeGetInfo info, String nodeId) {
        if (!info.getExistingNodeMap().containsKey(nodeId)) {
            rejectNotFound(info, nodeId);
            return false;
        }
        return true;
    }

    protected void addActiveElement(NodeGetInfo info, String nodeId, ElementJson indexElement) {
        info.getActiveElementMap().put(nodeId, indexElement);
    }

    protected List<String> getRefCommitIds(Instant time) {
        List<String> commitIds = new ArrayList<>();

        List<CommitJson> refCommits = commitPersistence.findByProjectAndRefAndTimestampAndLimit(getContext().getProjectId(), getContext().getBranchId(), time, 0);
        for (CommitJson c : refCommits) {
            commitIds.add(c.getId());
        }
        return commitIds;
    }

    public void addExistingElements(NodeGetInfo info, List<ElementJson> elements) {
        super.addExistingElements(info, elements);

        if(info instanceof FederatedNodeGetInfo) {
            FederatedNodeGetInfo federatedInfo = (FederatedNodeGetInfo)info;
            Set<String> elementIds = elements.stream().map(ElementJson::getId).collect(Collectors.toSet());
            List<Node> existingNodes = nodeDAO.findAllByNodeIds(elementIds);
            //ToDo : could also be done in get functions in respective classes to prevent extra null checks and NPE 
            federatedInfo.setExistingNodeMap(new HashMap<>());
            federatedInfo.setReqIndexIds(new HashSet<>());
            for (Node node : existingNodes) {
                federatedInfo.getReqIndexIds().add(node.getDocId());
                federatedInfo.getExistingNodeMap().put(node.getNodeId(), node);
                federatedInfo.getReqElementMap().put(node.getNodeId(), new ElementJson().setId(node.getNodeId()));
            }
        }
    }

    public NodeGetInfo createNodeGetInfo() {  //ToDo :: check 
        return new FederatedNodeGetInfoImpl();
    }
}
