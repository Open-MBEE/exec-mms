package org.openmbee.sdvc.crud.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.openmbee.sdvc.json.BaseJson;
import org.openmbee.sdvc.crud.domains.Node;

public class Helper {

    public static Map<String, ? extends BaseJson> convertJsonToMap(
        List<? extends BaseJson> elements) {
        Map<String, BaseJson> result = new HashMap<>();
        for (BaseJson elem : elements) {
            if (elem == null) {
                continue;
            }
            if (elem.getId() != null && !elem.getId().equals("")) {
                result.put(elem.getId(), elem);
            }
        }
        return result;
    }

    public static Map<String, Node> convertNodesToMap(List<Node> nodes) {
        Map<String, Node> result = new HashMap<>();
        for (Node node : nodes) {
            if (node == null) {
                continue;
            }
            if (node.getNodeId() != null && !node.getNodeId().equals("")) {
                result.put(node.getNodeId(), node);
            }
        }
        return result;
    }

    public static Map<String, Object> convertToMap(List<Map<String, Object>> elements, String key) {
        Map<String, Object> result = new HashMap<>();
        for (Map<String, Object> elem : elements) {
            if (elem == null) {
                continue;
            }
            String id = (String) elem.get(key);
            if (id != null && !id.equals("")) {
                result.put(id, elem);
            }
        }
        return result;
    }
}
