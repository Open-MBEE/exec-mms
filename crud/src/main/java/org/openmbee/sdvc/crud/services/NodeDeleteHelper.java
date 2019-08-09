package org.openmbee.sdvc.crud.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openmbee.sdvc.core.services.NodeChangeInfo;
import org.openmbee.sdvc.core.services.NodeService;
import org.openmbee.sdvc.data.domains.Node;
import org.openmbee.sdvc.json.CommitJson;
import org.openmbee.sdvc.json.ElementJson;
import org.springframework.stereotype.Service;

@Service
public class NodeDeleteHelper extends NodeOperation {

    public NodeChangeInfo processDeleteJson(List<ElementJson> elements, CommitJson cmjs, NodeService service) {
        NodeChangeInfo info = initInfo(elements, cmjs);

        for (String nodeId : info.getReqElementMap().keySet()) {
            if (!existingNodeContainsNodeId(info, nodeId)) {
                continue;
            }
            Node node = info.getExistingNodeMap().get(nodeId);
            Map<String, Object> indexElement = info.getExistingElementMap().get(nodeId);
            if (node.isDeleted()) {
                Map<String, Object> reject = new HashMap<>();
                reject.put("code", 304);
                reject.put("message", "Already deleted");
                reject.put("id", nodeId);
                reject.put("element", indexElement);
                info.getRejected().add(reject);
                continue;
            }
            info.getOldIndexIds().add(node.getIndexId());
            ElementJson request = info.getReqElementMap().get(nodeId);
            request.putAll(indexElement);
            info.getToSaveNodeMap().put(nodeId, node);
            processElementDeleted(request, node, info.getCommitJson());
            service.extraProcessDeletedElement(request, node, info);
            //TODO remove edges?
            info.getDeletedMap().put(nodeId, request);
        }
        return info;
    }
}
