package org.openmbee.sdvc.cameo.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.openmbee.sdvc.core.config.ContextHolder;
import org.openmbee.sdvc.core.objects.ElementsRequest;
import org.openmbee.sdvc.core.services.NodeChangeInfo;
import org.openmbee.sdvc.core.services.NodeGetInfo;
import org.openmbee.sdvc.json.ElementJson;
import org.openmbee.sdvc.core.objects.ElementsResponse;
import org.openmbee.sdvc.crud.services.DefaultNodeService;
import org.openmbee.sdvc.core.services.NodeService;
import org.openmbee.sdvc.data.domains.scoped.Node;
import org.openmbee.sdvc.cameo.CameoEdgeType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

@Service("cameoNodeService")
public class CameoNodeService extends DefaultNodeService implements NodeService {

    private CameoHelper cameoHelper;

    @Autowired
    public void setCameoHelper(CameoHelper cameoHelper) {
        this.cameoHelper = cameoHelper;
    }

    @Override
    public ElementsResponse read(String projectId, String refId, ElementsRequest req,
        Map<String, String> params) {
        //need to get mount hierarchy that handles circular dependencies

        String commitId = params.getOrDefault("commitId", null);
        ContextHolder.setContext(projectId, refId);
        logger.info("params: " + params);

        NodeGetInfo info = nodeGetHelper.processGetJson(req.getElements(), commitId, this);

        //need to continue to find rejected elements (410 or 404) in mounted projects and remove from rejected if found

        ElementsResponse response = new ElementsResponse();
        response.getElements().addAll(info.getActiveElementMap().values());
        response.setRejected(info.getRejected());
        return response;
    }

    @Override
    public void extraProcessPostedElement(ElementJson element, Node node, NodeChangeInfo info) {
        node.setNodeType(CameoHelper.getNodeType(element).getValue());
        //need to handle _childViews
        Map<Integer, List<Pair<String, String>>> res = info.getEdgesToSave();
        String owner = (String) element.get("ownerId");
        if (owner != null && !owner.isEmpty()) {
            if (!res.containsKey(CameoEdgeType.CONTAINMENT.getValue())) {
                res.put(CameoEdgeType.CONTAINMENT.getValue(), new ArrayList<>());
            }
            res.get(CameoEdgeType.CONTAINMENT.getValue()).add(Pair.of(owner, element.getId()));
        }
    }

    @Override
    public void extraProcessGotElement(ElementJson element, Node node, NodeGetInfo info) {
        //check if element is view, add in _childViews
    }
}
