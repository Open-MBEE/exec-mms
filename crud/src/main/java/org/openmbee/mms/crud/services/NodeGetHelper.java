package org.openmbee.mms.crud.services;

import java.time.Instant;
import java.util.*;

import java.util.stream.Collectors;

import org.openmbee.mms.core.config.ContextHolder;
import org.openmbee.mms.core.objects.Rejection;
import org.openmbee.mms.core.services.NodeGetInfo;
import org.openmbee.mms.core.exceptions.BadRequestException;
import org.openmbee.mms.core.services.NodeService;
import org.openmbee.mms.data.domains.scoped.Branch;
import org.openmbee.mms.data.domains.scoped.Commit;
import org.openmbee.mms.data.domains.scoped.Node;
import org.openmbee.mms.core.dao.CommitIndexDAO;
import org.openmbee.mms.json.CommitJson;
import org.openmbee.mms.json.ElementJson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static org.openmbee.mms.core.config.ContextHolder.getContext;

@Service
public class NodeGetHelper extends NodeOperation {

    protected CommitIndexDAO commitIndex;

    @Autowired
    public void setCommitIndex(CommitIndexDAO commitIndex) {
        this.commitIndex = commitIndex;
    }

    public NodeGetInfo processGetJsonFromNodes(List<Node> nodes, NodeService service) {
        NodeGetInfo info = initInfoFromNodes(nodes, null);
        return processLatest(info, service);
    }

    public NodeGetInfo processGetJson(List<ElementJson> elements, NodeService service) {
        NodeGetInfo info = initInfo(elements, null);
        return processLatest(info, service);
    }

    private NodeGetInfo processLatest(NodeGetInfo info, NodeService service) {
        for (String nodeId : info.getReqElementMap().keySet()) {
            if (!existingNodeContainsNodeId(info, nodeId)) {
                continue;
            }
            ElementJson indexElement = info.getExistingElementMap().get(nodeId);
            if (info.getExistingNodeMap().get(nodeId).isDeleted()) {
                rejectDeleted(info, nodeId, indexElement == null ? new ElementJson().setId(nodeId) : indexElement);
                continue;
            }
            if (indexElement == null) {
                logger.warn("node db and index mismatch on element get: nodeId: " + nodeId +
                    ", docId not found: " + info.getExistingNodeMap().get(nodeId).getDocId());
                rejectNotFound(info, nodeId);
                continue;
            }
            if (service != null) {
                service.extraProcessGotElement(indexElement, info.getExistingNodeMap().get(nodeId), info);
            }
            info.getActiveElementMap().put(nodeId, indexElement);
        }
        info.setCommitId(getLatestRefCommitId());
        return info;
    }

    public NodeGetInfo processGetJsonFromNodes(List<Node> nodes, String commitId, NodeService service) {
        if (commitId == null || commitId.isEmpty()) {
            return processGetJsonFromNodes(nodes, service);
        }
        NodeGetInfo info = initInfoFromNodes(nodes, null);
        return processCommit(info, commitId, service);
    }

    public NodeGetInfo processGetJson(List<ElementJson> elements, String commitId, NodeService service) {
        if (commitId == null || commitId.isEmpty()) {
            return processGetJson(elements, service);
        }
        NodeGetInfo info = initInfo(elements, null); //gets all current nodes
        return processCommit(info, commitId, service);
    }

    private NodeGetInfo processCommit(NodeGetInfo info, String commitId, NodeService service) {
        Optional<Commit> commit = commitRepository.findByCommitId(commitId);
        if (!commit.isPresent() ) {
            throw new BadRequestException("commitId is invalid");
        }
        info.setCommitId(commitId);
        Instant time = commit.get().getTimestamp(); //time of commit
        List<String> refCommitIds = null; //get it later if needed
        for (String nodeId : info.getReqElementMap().keySet()) {
            if (!existingNodeContainsNodeId(info, nodeId)) { // nodeId not found
                continue;
            }
            ElementJson indexElement = info.getExistingElementMap().get(nodeId);
            if (indexElement == null) {
                Node n = info.getExistingNodeMap().get(nodeId);
                logger.warn("node db and index mismatch on element commit get: nodeId: " + nodeId +
                    ", docId not found: " + n.getDocId());
                Optional<Commit> last = commitRepository.findByCommitId(n.getLastCommit());
                Optional<Commit> first = commitRepository.findByCommitId(n.getInitialCommit());
                if (!last.isPresent() || !first.isPresent()) {
                    rejectNotFound(info, nodeId);
                    continue;
                }
                indexElement = new ElementJson().setId(nodeId).setDocId(n.getDocId());
                indexElement.setModified(formatter.format(last.get().getTimestamp()));
                indexElement.setModifier(last.get().getCreator());
                indexElement.setCommitId(last.get().getCommitId());
                indexElement.setCreator(first.get().getCreator());
                indexElement.setCreated(formatter.format(first.get().getTimestamp()));
            }
            Instant modified = Instant.from(formatter.parse(indexElement.getModified()));
            Instant created = Instant.from(formatter.parse(indexElement.getCreated()));
            indexElement.setRefId(ContextHolder.getContext().getBranchId());
            if (commitId.equals(indexElement.getCommitId())) { //exact match
                info.getActiveElementMap().put(nodeId, indexElement);
            } else if (created.isAfter(time)) { // element created after commit
                rejectNotFound(info, nodeId);
            } else if (modified.isAfter(time)) { // latest element is after commit
                Optional<ElementJson> tryExact = nodeIndex.getByCommitId(commitId, nodeId);
                if (tryExact.isPresent()) {
                    tryExact.get().setRefId(ContextHolder.getContext().getBranchId());
                    info.getActiveElementMap().put(nodeId, tryExact.get());
                    continue; // found exact match at commit
                }
                if (refCommitIds == null) { // need list of commitIds of current ref to filter on
                    refCommitIds = getRefCommitIds(time);
                }
                Optional<ElementJson> e = nodeIndex.getElementLessThanOrEqualTimestamp(nodeId,
                    formatter.format(time), refCommitIds);
                if (e.isPresent()) { // found version of element at commit time
                    Instant realModified = Instant.from(formatter.parse(e.get().getModified()));
                    if (elementDeleted(nodeId, commitId, time, realModified, refCommitIds)) {
                        rejectDeleted(info, nodeId, e.get());
                    } else {
                        e.get().setRefId(ContextHolder.getContext().getBranchId());
                        info.getActiveElementMap().put(nodeId, e.get());
                    }
                } else {
                    rejectNotFound(info, nodeId); // element not found at commit time
                }
            } else if (info.getExistingNodeMap().get(nodeId).isDeleted()) { // latest element is before commit, but deleted
                if (refCommitIds == null) { // need list of commitIds of current ref to filter on
                    refCommitIds = getRefCommitIds(time);
                }
                if (elementDeleted(nodeId, commitId, time, modified, refCommitIds)) {
                    rejectDeleted(info, nodeId, indexElement);
                } else {
                    info.getActiveElementMap().put(nodeId, indexElement);
                }
            } else { // latest element version is version at commit, not deleted
                info.getActiveElementMap().put(nodeId, indexElement);
            }
        }
        return info;
    }

    private boolean elementDeleted(String nodeId, String commitId, Instant time, Instant modified, List<String> refCommitIds) {
        List<CommitJson> commits = commitIndex.elementDeletedHistory(nodeId, refCommitIds);
        for (CommitJson c: commits) {
            Instant deletedTime = Instant.from(formatter.parse(c.getCreated()));
            if ((deletedTime.isBefore(time) || c.getId().equals(commitId)) && deletedTime.isAfter(modified)) {
                //there's a delete between element last modified time and requested commit time
                //or element is deleted at commit
                return true;
            }
        }
        return false;
    }

    public NodeGetInfo processGetJson(List<ElementJson> elements, Instant time, NodeService service) {
        Optional<Branch> ref = branchRepository.findByBranchId(getContext().getBranchId());
        if (ref.isPresent()) {
            Optional<Commit> c = commitRepository.findLatestByRef(ref.get());
            if (c.isPresent()) {
                return processGetJson(elements, c.get().getCommitId(), service);
            } else {
                throw new BadRequestException("invalid time");
            }
        }
        return null;
    }

    public List<ElementJson> processGetAll() {
        Set<String> indexIds = new HashSet<>();
        List<Node> existingNodes = nodeRepository.findAllByDeleted(false);
        for (Node node : existingNodes) {
            indexIds.add(node.getDocId());
        }
        List<ElementJson> els = nodeIndex.findAllById(indexIds);
        String ref = ContextHolder.getContext().getBranchId();
        for (ElementJson el: els) {
            el.setRefId(ref);
        }
        return els;
    }

    public List<ElementJson> processGetAll(String commitId, NodeService service) {
        if (commitId == null || commitId.isEmpty()) {
            return processGetAll();
        } else {
            List<Node> nodes = nodeRepository.findAll();
            List<ElementJson> el = nodes.stream().map(
                node -> new ElementJson().setId(node.getNodeId())).collect(Collectors.toList());
            NodeGetInfo info = processGetJson(el, commitId, service);
            return new ArrayList<>(info.getActiveElementMap().values());
        }
    }

    public List<ElementJson> processGetAll(Instant time, NodeService service) {
        List<ElementJson> result = new ArrayList<>();
        Optional<Branch> ref = branchRepository.findByBranchId(getContext().getBranchId());
        if (ref.isPresent()) {
            Optional<Commit> c = commitRepository.findByRefAndTimestamp(ref.get(), time);
            if (c.isPresent()) {
                result.addAll(processGetAll(c.get().getCommitId(), service));
            } else {
                throw new BadRequestException("invalid time");
            }
        }
        return result;
    }

    protected void rejectDeleted(NodeGetInfo info, String nodeId, ElementJson indexElement) {
        info.addRejection(nodeId, new Rejection(indexElement, 410, "Element deleted"));
    }

    protected List<String> getRefCommitIds(Instant time) {
        List<String> commitIds = new ArrayList<>();

        Optional<Branch> ref = branchRepository.findByBranchId(getContext().getBranchId());
        ref.ifPresent(current -> {
            List<Commit> refCommits = commitRepository.findByRefAndTimestampAndLimit(current, time, 0);
            for (Commit c : refCommits) {
                commitIds.add(c.getCommitId());
            }
        });
        return commitIds;
    }
}