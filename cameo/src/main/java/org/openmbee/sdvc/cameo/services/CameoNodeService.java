package org.openmbee.sdvc.cameo.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.openmbee.sdvc.crud.services.NodeChangeInfo;
import org.openmbee.sdvc.json.ElementJson;
import org.openmbee.sdvc.crud.controllers.elements.ElementsResponse;
import org.openmbee.sdvc.crud.domains.Node;
import org.openmbee.sdvc.crud.services.DefaultNodeService;
import org.openmbee.sdvc.crud.services.NodeService;
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
    public ElementsResponse read(String projectId, String refId, String id,
        Map<String, String> params) {
        //need to use mount search to accommodate depth param and project mounts
        //add childviews and extended info
        return super.read(projectId, refId, id, params);
    }

    @Override
    public void extraProcessPostedElement(ElementJson element, Node node, NodeChangeInfo info) {
        node.setNodeType(CameoHelper.getNodeType(element).getValue());
        Map<Integer, List<Pair<String, String>>> res = info.getEdgesToSave();
        String owner = (String) element.get("ownerId");
        if (owner != null && !owner.isEmpty()) {
            if (!res.containsKey(CameoEdgeType.CONTAINMENT.getValue())) {
                res.put(CameoEdgeType.CONTAINMENT.getValue(), new ArrayList<>());
            }
            res.get(CameoEdgeType.CONTAINMENT.getValue()).add(Pair.of(owner, element.getId()));
        }
    }
}
