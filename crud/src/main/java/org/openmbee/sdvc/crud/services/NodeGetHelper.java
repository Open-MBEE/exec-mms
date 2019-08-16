package org.openmbee.sdvc.crud.services;

import java.time.Instant;
import java.util.*;

import org.openmbee.sdvc.core.services.NodeGetInfo;
import org.openmbee.sdvc.core.config.ContextHolder;
import org.openmbee.sdvc.crud.exceptions.BadRequestException;
import org.openmbee.sdvc.data.domains.Branch;
import org.openmbee.sdvc.data.domains.Commit;
import org.openmbee.sdvc.data.domains.Node;
import org.openmbee.sdvc.json.ElementJson;
import org.springframework.stereotype.Service;

import static org.openmbee.sdvc.core.config.ContextHolder.getContext;

@Service
public class NodeGetHelper extends NodeOperation {

    public NodeGetInfo processGetJson(List<ElementJson> elements) {
        NodeGetInfo info = initInfo(elements, null);

        for (String nodeId : info.getReqElementMap().keySet()) {
            if (!existingNodeContainsNodeId(info, nodeId)) {
                continue;
            }
            ElementJson indexElement = info.getExistingElementMap().get(nodeId);
            if (info.getExistingNodeMap().get(nodeId).isDeleted()) {
                rejectDeleted(info, nodeId, indexElement);
                continue;
            }
            info.getActiveElementMap().put(nodeId, indexElement);
        }
        return info;
    }

    public NodeGetInfo processGetJson(List<ElementJson> elements, String commitId) {
        if (commitId == null || commitId.isEmpty()) {
            return processGetJson(elements);
        }

        Optional<Commit> commit = commitRepository.findByCommitId(commitId);
        Optional<Branch> currentBranch = branchRepository.findByBranchId(ContextHolder.getContext().getBranchId());
        if (!commit.isPresent() ) { //TODO also if commitId is not part of current branch history?
            throw new BadRequestException("commitId is invalid");
        }
        Instant time = commit.get().getTimestamp(); //time of commit
        List<String> refCommitIds = null; //get it later if needed

        NodeGetInfo info = initInfo(elements, null); //gets all current nodes
        for (String nodeId : info.getReqElementMap().keySet()) {
            if (!existingNodeContainsNodeId(info, nodeId)) { // nodeId not found
                continue;
            }
            ElementJson indexElement = info.getExistingElementMap().get(nodeId);
            Instant modified = Instant.from(formatter.parse(indexElement.getModified()));
            Instant created = Instant.from(formatter.parse(indexElement.getCreated()));

            if (commitId.equals(indexElement.getCommitId())) { //exact match
                info.getActiveElementMap().put(nodeId, indexElement);
            } else if (created.isAfter(time)) { // element created after commit
                rejectNotFound(info, nodeId);
            } else if (modified.isAfter(time)) { // latest element is after commit
                Optional<ElementJson> tryExact = nodeIndex.getByCommitId(commitId, nodeId);
                if (tryExact.isPresent()) {
                    info.getActiveElementMap().put(nodeId, tryExact.get());
                    continue; // found exact match at commit
                }
                if (refCommitIds == null) { // need list of commitIds of current ref to filter on
                    refCommitIds = getRefCommitIds(time);
                }
                Optional<ElementJson> e = nodeIndex.getElementLessThanOrEqualTimestamp(nodeId,
                    formatter.format(time), refCommitIds);
                if (e.isPresent()) { // found version of element at commit time
                    //TODO determine if element was deleted at the time?
                    info.getActiveElementMap().put(nodeId, e.get());
                } else {
                    rejectNotFound(info, nodeId); // element not found at commit time
                }
            } else if (info.getExistingNodeMap().get(nodeId).isDeleted()) { // latest element is before commit, but deleted
                rejectDeleted(info, nodeId, indexElement);
            } else { // latest element version is version at commit, not deleted
                info.getActiveElementMap().put(nodeId, indexElement);
            }
        }
        return info;
    }

    public NodeGetInfo processGetJson(List<ElementJson> elements, Instant time) {
        Optional<Branch> ref = branchRepository.findByBranchId(getContext().getBranchId());
        if (ref.isPresent()) {
            Optional<Commit> c = commitRepository.findLatestByRef(ref.get());
            if (c.isPresent()) {
                return processGetJson(elements, c.get().getDocId());
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
        return nodeIndex.findAllById(indexIds);
    }

    public List<ElementJson> processGetAll(String commitId) {
        if (commitId == null || commitId.isEmpty()) {
            return processGetAll();
        }
        //TODO (basically get model at commit)
        return processGetAll();
    }

    public List<ElementJson> processGetAll(Instant time) {
        List<ElementJson> result = new ArrayList<>();
        Optional<Branch> ref = branchRepository.findByBranchId(getContext().getBranchId());
        if (ref.isPresent()) {
            Optional<Commit> c = commitRepository.findByRefAndTimestamp(ref.get(), time);
            if (c.isPresent()) {
                result.addAll(processGetAll(c.get().getDocId()));
            } else {
                throw new BadRequestException("invalid time");
            }
        }
        return result;
    }

    protected void rejectDeleted(NodeGetInfo info, String nodeId, ElementJson indexElement) {
        Map<String, Object> reject = new HashMap<>();
        reject.put("code", 410);
        reject.put("message", "Element deleted");
        reject.put("id", nodeId);
        reject.put("element", indexElement);
        info.getRejected().add(reject);
    }

    protected List<String> getRefCommitIds(Instant time) {
        List<String> commitIds = new ArrayList<>();

        Optional<Branch> ref = branchRepository.findByBranchId(getContext().getBranchId());
        ref.ifPresent(current -> {
            List<Commit> refCommits = commitRepository.findByRefAndTimestampAndLimit(current, time, 0);
            for (Commit c : refCommits) {
                commitIds.add(c.getDocId());
            }
        });
        return commitIds;
    }
}