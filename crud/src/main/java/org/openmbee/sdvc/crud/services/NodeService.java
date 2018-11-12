package org.openmbee.sdvc.crud.services;

import java.util.Map;
import org.openmbee.sdvc.crud.controllers.elements.ElementsRequest;
import org.openmbee.sdvc.crud.controllers.elements.ElementsResponse;

public interface NodeService {

    ElementsResponse get(String projectId, String refId, String id, Map<String, String> params);

    ElementsResponse post(String projectId, String refId, ElementsRequest req,
        Map<String, String> params);
}
