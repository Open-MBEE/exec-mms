package org.openmbee.sdvc.crud.services;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.openmbee.sdvc.json.CommitJson;
import org.openmbee.sdvc.json.ElementJson;
import org.openmbee.sdvc.crud.controllers.elements.ElementsRequest;
import org.openmbee.sdvc.crud.controllers.elements.ElementsResponse;
import org.openmbee.sdvc.crud.domains.Node;
import org.springframework.data.util.Pair;

public interface NodeService {

    ElementsResponse get(String projectId, String refId, String id, Map<String, String> params);

    ElementsResponse post(String projectId, String refId, ElementsRequest req,
        Map<String, String> params);

    void extraProcessPostedElement(ElementJson element, Node node,
        Set<String> oldElasticIds, CommitJson cmjs, Instant now, Map<String, Node> toSave,
        Map<String, ElementJson> response);

    Map<Integer, List<Pair<String, String>>> getEdgeInfo(Collection<ElementJson> elements);
}
