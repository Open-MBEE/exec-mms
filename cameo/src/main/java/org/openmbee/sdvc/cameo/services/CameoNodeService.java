package org.openmbee.sdvc.cameo.services;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.openmbee.sdvc.json.CommitJson;
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
    public ElementsResponse get(String projectId, String refId, String id,
        Map<String, String> params) {
        //need to use mount search to accommodate depth param and project mounts
        //add childviews and extended info
        return super.get(projectId, refId, id, params);
    }

    @Override
    public void extraProcessPostedElement(ElementJson element, Node node,
        Set<String> oldElasticIds, CommitJson cmjs, Instant now, Map<String, Node> toSave,
        Map<String, ElementJson> response) {

        node.setNodeType(CameoHelper.getNodeType(element).getValue());
    }

    @Override
    public Map<Integer, List<Pair<String, String>>> getEdgeInfo(Collection<ElementJson> elements) {
        Map<Integer, List<Pair<String, String>>> res = new HashMap<>();
        for (ElementJson e: elements) {
            String owner = (String) e.get("ownerId");
            if (owner != null && !owner.isEmpty()) {
                if (!res.containsKey(CameoEdgeType.CONTAINMENT.getValue())) {
                    res.put(CameoEdgeType.CONTAINMENT.getValue(), new ArrayList<>());
                }
                res.get(CameoEdgeType.CONTAINMENT.getValue()).add(Pair.of(owner, e.getId()));
            }
        }
        //TODO when to delete existing edges
        return res;
    }
}
