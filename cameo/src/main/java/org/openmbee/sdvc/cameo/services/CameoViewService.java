package org.openmbee.sdvc.cameo.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.openmbee.sdvc.cameo.CameoConstants;
import org.openmbee.sdvc.cameo.CameoNodeType;
import org.openmbee.sdvc.core.config.ContextHolder;
import org.openmbee.sdvc.core.objects.ElementsRequest;
import org.openmbee.sdvc.core.objects.ElementsResponse;
import org.openmbee.sdvc.core.services.NodeChangeInfo;
import org.openmbee.sdvc.data.domains.scoped.Node;
import org.openmbee.sdvc.json.BaseJson;
import org.openmbee.sdvc.json.ElementJson;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

@Service("cameoViewService")
public class CameoViewService extends CameoNodeService {

    public ElementsResponse getDocuments(String projectId, String refId, Map<String, String> params) {
        ContextHolder.setContext(projectId, refId);
        List<Node> documents = this.nodeRepository.findAllByNodeType(CameoNodeType.DOCUMENT.getValue());
        ElementsResponse res = this.getViews(projectId, refId, buildRequest(nodeGetHelper.convertNodesToMap(documents).keySet()), params);
        for (ElementJson e: res.getElements()) {
            Optional<ElementJson> parent = nodeGetHelper.getFirstRelationshipOfType(e, CameoNodeType.GROUP.getValue(), CameoConstants.OWNERID);
            if (parent.isPresent()) {
                e.put("_groupId", parent.get().getId());
            }
        }
        return res;
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
                    if ("Property".equals(attr.getType()) && childId != null && !childId.isEmpty()) {
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

    public ElementsResponse getGroups(String projectId, String refId, Map<String, String> params) {
        ContextHolder.setContext(projectId, refId);
        List<Node> groups = this.nodeRepository.findAllByNodeType(CameoNodeType.GROUP.getValue());
        ElementsResponse res = this.read(projectId, refId, buildRequest(nodeGetHelper.convertNodesToMap(groups).keySet()), params);
        for (ElementJson e: res.getElements()) {
            Optional<ElementJson> parent = nodeGetHelper.getFirstRelationshipOfType(e, CameoNodeType.GROUP.getValue(), CameoConstants.OWNERID);
            if (parent.isPresent()) {
                e.put("_parentId", parent.get().getId());
            }
        }
        return res;
}

    @Override
    public void extraProcessPostedElement(ElementJson element, Node node, NodeChangeInfo info) {
        //handle _childViews
        List<Map<String, String>> newChildViews = (List)element.remove(CameoConstants.CHILDVIEWS);
        if (newChildViews == null) {
            super.extraProcessPostedElement(element, node, info);
            return;
        }
        List<String> oldOwnedAttributeIds = (List)element.get(CameoConstants.OWNEDATTRIBUTEIDS);
        List<Pair<String, String>> newChildViewIds = new ArrayList<>();
        for (Map<String, String> newChildView: newChildViews) {
            newChildViewIds.add(Pair.of(newChildView.get(ElementJson.ID), newChildView.get(CameoConstants.AGGREGATION)));
        }
        Map<String, ElementJson> oldOwnedAttributes = nodeGetHelper.processGetJson(buildRequest(oldOwnedAttributeIds).getElements(), null).getActiveElementMap();

        List<Pair<String, String>> oldChildViews = new ArrayList<>();
        List<String> oldChildViewIds = new ArrayList<>();
        List<String> oldNonViewAttributeIds = new ArrayList<>();
        List<String> newOwnedAttributeIds = new ArrayList<>();
        List<String> oldOwnedAttributeIdsToDelete = new ArrayList<>();
        List<Pair<String, String>> newChildViewsToCreate = new ArrayList<>();
        for (String oldOwnedAttributeId: oldOwnedAttributeIds) {
            ElementJson oldOwnedAttribute = oldOwnedAttributes.get(oldOwnedAttributeId);
            if (oldOwnedAttribute == null) {
                continue;
            }
            String type = (String)oldOwnedAttribute.get(CameoConstants.TYPEID);
            if (type == null || type.isEmpty()) {
                oldNonViewAttributeIds.add(oldOwnedAttributeId);
                continue;
            }
            oldChildViewIds.add(type);
        }

        super.extraProcessPostedElement(element, node, info);
    }
}
