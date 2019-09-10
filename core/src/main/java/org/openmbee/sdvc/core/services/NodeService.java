package org.openmbee.sdvc.core.services;

import java.util.Map;

import org.openmbee.sdvc.core.objects.ElementsRequest;
import org.openmbee.sdvc.core.objects.ElementsResponse;
import org.openmbee.sdvc.data.domains.scoped.Node;
import org.openmbee.sdvc.json.ElementJson;

public interface NodeService {

    ElementsResponse read(String projectId, String refId, String id, Map<String, String> params);

    ElementsResponse read(String projectId, String refId, ElementsRequest req, Map<String, String> params);

    ElementsResponse createOrUpdate(String projectId, String refId, ElementsRequest req,
        Map<String, String> params);

    void extraProcessPostedElement(ElementJson element, Node node, NodeChangeInfo info);

    void extraProcessDeletedElement(ElementJson element, Node node, NodeChangeInfo info);

    ElementsResponse delete(String projectId, String refId, String id);

    ElementsResponse delete(String projectId, String refId, ElementsRequest req);
}
