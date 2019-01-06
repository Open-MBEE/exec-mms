package org.openmbee.sdvc.crud.services;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.openmbee.sdvc.crud.domains.Node;
import org.openmbee.sdvc.json.ElementJson;
import org.springframework.stereotype.Service;

@Service
public class NodeGetHelper extends NodeOperation {

    public NodeGetInfo processGetJson(List<ElementJson> elements) {
        NodeGetInfo info = initInfo(elements, null);

        for (String nodeId : info.getReqElementMap().keySet()) {
            if (!existingNodeContainsNodeId(info, nodeId)) {
                continue;
            }
            Node node = info.getExistingNodeMap().get(nodeId);
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
