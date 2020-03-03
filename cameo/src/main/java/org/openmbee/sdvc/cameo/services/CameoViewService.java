package org.openmbee.sdvc.cameo.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.openmbee.sdvc.cameo.CameoConstants;
import org.openmbee.sdvc.cameo.CameoNodeType;
import org.openmbee.sdvc.core.config.ContextHolder;
import org.openmbee.sdvc.core.objects.ElementsRequest;
import org.openmbee.sdvc.core.objects.ElementsResponse;
import org.openmbee.sdvc.data.domains.scoped.Node;
import org.openmbee.sdvc.json.BaseJson;
import org.openmbee.sdvc.json.ElementJson;
import org.springframework.stereotype.Service;

@Service("cameoViewService")
public class CameoViewService extends CameoNodeService {

    public ElementsResponse getDocuments(String projectId, String refId, Map<String, String> params) {
        ContextHolder.setContext(projectId, refId);
        List<Node> documents = this.nodeRepository.findAllByNodeType(CameoNodeType.DOCUMENT.getValue());
        return this.getViews(projectId, refId, buildRequest(nodeGetHelper.convertNodesToMap(documents).keySet()), params);
    }

    public ElementsResponse getView(String projectId, String refId, String elementId, Map<String, String> params) {
        return this.getViews(projectId, refId, buildRequest(elementId), params);
    }

    public ElementsResponse getViews(String projectId, String refId, ElementsRequest req, Map<String, String> params) {
        ElementsResponse res = this.read(projectId, refId, req, params);
        for (ElementJson element: res.getElements()) {
            if (cameoHelper.isView(element)) {
                List<String> ownedAttributeIds = (List) element.get(CameoConstants.OWNEDATTRIBUTEIDS);
                ElementsResponse ownedAttributes = this.read(element.getProjectId(), element.getRefId(),
                    buildRequest(ownedAttributeIds), params);
                List<ElementJson> sorted = nodeGetHelper.sort(ownedAttributeIds, ownedAttributes.getElements());
                List<Map> childViews = new ArrayList<>();
                for (ElementJson attr : sorted) {
                    String childId = (String) attr.get(CameoConstants.TYPEID);
                    if ("Property".equals(attr.getType()) && childId != null && !"".equals(childId)) {
                        Map<String, String> child = new HashMap<>();
                        child.put(BaseJson.ID, childId);
                        child.put(CameoConstants.AGGREGATION, (String) attr.get(CameoConstants.AGGREGATION));
                        child.put(CameoConstants.PROPERTYID, attr.getId());
                        childViews.add(child);
                    }
                }
                element.put(CameoConstants.CHILDVIEWS, childViews);
            }
        }
        return res;
    }
}
