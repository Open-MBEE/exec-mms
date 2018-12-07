package org.openmbee.sdvc.crud.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openmbee.sdvc.crud.controllers.BaseJson;
import org.openmbee.sdvc.crud.controllers.elements.ElementJson;

public class NodePostHelper {
    protected static final Logger logger = LogManager.getLogger(NodePostHelper.class);

    public static Map<String, Object> convertToMap(List<ElementJson> elements) {
        Map<String, Object> result = new HashMap<>();
        for (int i = 0; i < elements.size(); i++) {
            ElementJson elem = elements.get(i);
            if (!elem.getId().equals("")) {
                result.put(elem.getId(), elem);
            }
        }

        return result;
    }
    public static Map<String, Object> convertListToMap(List<Map<String, Object>> elements) {
        Map<String, Object> result = new HashMap<>();
        for (int i = 0; i < elements.size(); i++) {
            BaseJson elem = (BaseJson) elements.get(i);
            if (!elem.getId().equals("")) {
                result.put(elem.getId(), elem);
            }
        }

        return result;
    }

    public static boolean isUpdated(BaseJson element, Map<String, Object> existing, Map<Integer, String> rejection) {
        if (existing == null) {
            return false;
        }

//        if (logger.isDebugEnabled()) {
//            logger.debug("New Element: " + element);
//            logger.debug("Old Element: " + existing);
//        }

//        Map<String, Object> newElement = toMap(element);
//        Map<String, Object> oldElement = toMap(existing);

//        boolean equiv = isEquivalent(newElement, oldElement);
        boolean equiv = isEquivalent(element, existing);
//
        if (equiv) {
            rejection.put(HttpServletResponse.SC_NOT_MODIFIED, "Is Equivalent");
        }

        return !equiv;
//        return true;
    }

    private static boolean isEquivalent(Map<String, Object> map1, Map<String, Object> map2) {
        for (Map.Entry<String, Object> entry : map1.entrySet()) {
            Object value1 = entry.getValue();
            Object value2 = map2.get(entry.getKey());
            if (logger.isDebugEnabled()) {
                logger.debug("Value 1: " + value1);
                logger.debug("Value 2: " + value2);
            }
            if (value1 == null && value2 != null) {
                return false;
            }
            if (value1 instanceof Map) {
                if (!(value2 instanceof Map)) {
                    return false;
                } else {
                    if (logger.isDebugEnabled()) {
                        logger.debug("Is Equivalent: " + isEquivalent((Map<String, Object>) value1,
                            (Map<String, Object>) value2));
                    }
                    if (!isEquivalent((Map<String, Object>) value1, (Map<String, Object>) value2)) {
                        return false;
                    }
                }
            } else if (value1 instanceof List) {
                if (!(value2 instanceof List)) {
                    return false;
                } else {
                    if (logger.isDebugEnabled()) {
                        logger.debug("Is Equivalent: " + isEquivalent((List<Object>) value1, (List<Object>) value2));
                    }
                    if (!isEquivalent((List<Object>) value1, (List<Object>) value2)) {
                        return false;
                    }
                }
            } else {
                if (logger.isDebugEnabled()) {
                    logger.debug("Is Equivalent: " + value1.equals(value2));
                }
                if (!value1.equals(value2)) {
                    return false;
                }
            }
//            only need to chceck value 1 to make sure it's not null - otherwise combine last 4 into one
        }

        return true;
    }

    private static boolean isEquivalent(List<Object> list1, List<Object> list2) {
        if (list1.size() != list2.size()) {
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
