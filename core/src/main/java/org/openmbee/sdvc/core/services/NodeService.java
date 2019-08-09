package org.openmbee.sdvc.core.services;

import java.util.Map;
import org.openmbee.sdvc.data.domains.Node;
import org.openmbee.sdvc.json.ElementJson;

public interface NodeService<T, U> {

    T read(String projectId, String refId, String id, Map<String, String> params);

    T read(String projectId, String refId, U req, Map<String, String> params);

    T createOrUpdate(String projectId, String refId, U req,
        Map<String, String> params);

    void extraProcessPostedElement(ElementJson element, Node node, NodeChangeInfo info);

    void extraProcessDeletedElement(ElementJson element, Node node, NodeChangeInfo info);

    T delete(String projectId, String refId, String id);

    T delete(String projectId, String refId, U req);
}
