package org.openmbee.sdvc.crud.services;

import java.util.*;

import org.openmbee.sdvc.crud.config.DbContextHolder;
import org.openmbee.sdvc.data.domains.Commit;
import org.openmbee.sdvc.data.domains.Node;
import org.openmbee.sdvc.json.ElementJson;
import org.springframework.stereotype.Service;

@Service
public class NodeGetHelper extends NodeOperation {

    public NodeGetInfo processGetJson(List<ElementJson> elements, String commitId) {
        NodeGetInfo info = initInfo(elements, null);

        for (String nodeId : info.getReqElementMap().keySet()) {
            if (!existingNodeContainsNodeId(info, nodeId)) {
                continue;
            }
            Node node = info.getExistingNodeMap().get(nodeId);
            if (commitId != null) {
                Optional<ElementJson> atCommit = nodeIndex.getByCommitId(nodeId, commitId);
                if (!atCommit.isPresent()) {
                    Optional<Commit> commitObj = commitRepository.findByCommitId(commitId);
                    if (!commitObj.isPresent()) {
                        Map<String, Object> reject = new HashMap<>();
                        reject.put("code", 404);
                        reject.put("message", "Commit not found!");
                        reject.put("id", nodeId);
                        reject.put("element", null);
                        info.getRejected().add(reject);
                        continue;
                    }
                    List<String> refList = new ArrayList<>();
                    refList.add(DbContextHolder.getContext().getBranchId());
                    atCommit = nodeIndex.getElementLessThanOrEqualTimestamp(nodeId, commitObj.get().getTimestamp().toString(), refList);
                    if (!atCommit.isPresent()) {
                        Map<String, Object> reject = new HashMap<>();
                        reject.put("code", 404);
                        reject.put("message", "Element not found!");
                        reject.put("id", nodeId);
                        reject.put("element", atCommit);
                        info.getRejected().add(reject);
                        continue;
                    }
                }
                info.getActiveElementMap().put(nodeId, atCommit.get());
            }
            ElementJson indexElement = info.getExistingElementMap().get(nodeId);
            if (node.isDeleted()) {
                Map<String, Object> reject = new HashMap<>();
                reject.put("code", 410);
                reject.put("message", "Element deleted");
                reject.put("id", nodeId);
                reject.put("element", indexElement);
                info.getRejected().add(reject);
                continue;
            }
            info.getActiveElementMap().put(nodeId, indexElement);
        }
        return info;
    }

    public List<ElementJson> processGetAll() {
        Set<String> indexIds = new HashSet<>();
        List<Node> existingNodes = nodeRepository.findAll();
        for (Node node : existingNodes) {
            indexIds.add(node.getIndexId());
        }
        return nodeIndex.findAllById(indexIds);
    }
}
