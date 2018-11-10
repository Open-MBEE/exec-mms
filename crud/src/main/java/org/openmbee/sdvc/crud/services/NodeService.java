package org.openmbee.sdvc.crud.services;

import org.openmbee.sdvc.crud.controllers.elements.ElementsRequest;
import org.openmbee.sdvc.crud.controllers.elements.ElementsResponse;

import java.util.Map;

public interface NodeService {
    ElementsResponse get(String projectId, String refId, String id, Map<String, String> params);
    ElementsResponse post(String projectId, String refId, ElementsRequest req, Map<String, String> params);
}
