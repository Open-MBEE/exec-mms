package org.openmbee.sdvc.crud.services;

import java.util.Map;
import org.openmbee.sdvc.json.ElementJson;
import org.openmbee.sdvc.crud.controllers.elements.ElementsRequest;
import org.openmbee.sdvc.crud.controllers.elements.ElementsResponse;
import org.openmbee.sdvc.crud.domains.Node;

public interface NodeService {

    ElementsResponse get(String projectId, String refId, String id, Map<String, String> params);

    ElementsResponse get(String projectId, String refId, ElementsRequest req, Map<String, String> params);

    ElementsResponse post(String projectId, String refId, ElementsRequest req,
        Map<String, String> params);

    void extraProcessPostedElement(ElementJson element, Node node, NodeChangeInfo info);

    void extraProcessDeletedElement(ElementJson element, Node node, NodeChangeInfo info);

    ElementsResponse delete(String projectId, String refId, String id);

    ElementsResponse delete(String projectId, String refId, ElementsRequest req);
}
