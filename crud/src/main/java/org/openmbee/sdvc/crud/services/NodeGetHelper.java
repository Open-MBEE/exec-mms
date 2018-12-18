package org.openmbee.sdvc.crud.services;

import java.util.ArrayList;
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
            Map<String, Object> indexElement = info.getExistingElementMap().get(nodeId);
            if (node.isDeleted()) {
                Map<String, Object> reject = new HashMap<>();
                reject.put("code", 410);
                reject.put("message", "Element deleted");
                reject.put("id", nodeId);
                reject.put("element", indexElement);
                info.getRejected().add(reject);
                continue;
            }
        }
        return info;
    }

    public Map<String, Map<String, Object>> processGetAll() {
        Set<String> indexIds = new HashSet<>();
        List<Node> existingNodes = nodeRepository.findAll();
        Map<String, Node> existingNodeMap = new HashMap<>();
        for (Node node : existingNodes) {
            indexIds.add(node.getIndexId());
            existingNodeMap.put(node.getNodeId(), node);
        }
        // bulk get existing elements in elastic
        List<Map<String, Object>> existingElements = nodeIndex.findByIndexIds(indexIds);
        Map<String, Map<String, Object>> existingElementMap = Helper
            .convertToMap(existingElements, ElementJson.ID);

        return existingElementMap;
    }
}
