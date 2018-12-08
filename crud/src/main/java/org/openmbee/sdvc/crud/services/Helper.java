package org.openmbee.sdvc.crud.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.openmbee.sdvc.crud.controllers.BaseJson;
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
            if (node.getSysmlId() != null && !node.getSysmlId().equals("")) {
                result.put(node.getSysmlId(), node);
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

    public static boolean isEquivalent(Map<String, Object> map1, Map<String, Object> map2) {
        if (map1 == map2) {
            return true;
        }
        if (map1 == null || map2 == null) {
            return false;
        }
        for (Map.Entry<String, Object> entry : map1.entrySet()) {
            if (!map2.containsKey(entry.getKey())) {
                return false;
            }
            Object value1 = entry.getValue();
            Object value2 = map2.get(entry.getKey());
            if (value1 == null && value2 != null) {
                return false;
            }
            if (value1 == value2) {
                continue;
            }
            if (value1 instanceof Map) {
                if (!(value2 instanceof Map)) {
                    return false;
                } else {
                    if (!isEquivalent((Map<String, Object>) value1, (Map<String, Object>) value2)) {
                        return false;
                    }
                }
            } else if (value1 instanceof List) {
                if (!(value2 instanceof List)) {
                    return false;
                } else {
                    if (!isEquivalent((List<Object>) value1, (List<Object>) value2)) {
                        return false;
                    }
                }
            } else if (!value1.equals(value2)) {
                return false;
            }
        }
        return true;
    }

    public static boolean isEquivalent(List<Object> list1, List<Object> list2) {
        if (list1 == list2) {
            return true;
        }
        if (list1 == null || list2 == null || list1.size() != list2.size()) {
            return false;
        }

        for (int i = 0; i < list1.size(); i++) {
            Map<String, Object> toTestMap = new HashMap<>();
            Map<String, Object> testAgainstMap = new HashMap<>();

            toTestMap.put("fromList", list1.get(i));
            testAgainstMap.put("fromList", list2.get(i));

            if (!isEquivalent(toTestMap, testAgainstMap)) {
                return false;
            }
        }
        return true;
    }
}
