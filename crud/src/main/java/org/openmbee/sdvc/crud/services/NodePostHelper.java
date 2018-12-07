package org.openmbee.sdvc.crud.services;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openmbee.sdvc.crud.config.DbContextHolder;
import org.openmbee.sdvc.crud.controllers.BaseJson;
import org.openmbee.sdvc.crud.controllers.commits.CommitJson;
import org.openmbee.sdvc.crud.controllers.elements.ElementJson;
import org.openmbee.sdvc.crud.controllers.elements.ElementsResponse;
import org.openmbee.sdvc.crud.domains.Node;
import org.openmbee.sdvc.crud.domains.NodeType;

public class NodePostHelper {

    protected static final Logger logger = LogManager.getLogger(NodePostHelper.class);
    public static SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

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

    // create new elastic id for all element json, update modified time, modifier (use dummy for now), set _projectId, _refId, _inRefIds
    public static List<Node> processPostJson(List<ElementJson> elements,
        Map<String, Object> elasticNodeMap,
        Set<String> elasticIds, List<Map<String, Object>> rejectedList, boolean overwriteJson,
        Instant now,
        List<Map<String, Object>> commitAdded, List<Map<String, Object>> commitUpdated,
        List<Map<String, Object>> commitDeleted,
        String commitId, ElementsResponse response, Map<String, Object> exisitingNodeMap) {

        List<Node> toSave = new ArrayList<>();
        // Logic for update/add
        for (ElementJson element : elements) {

            BaseJson elasticElement = (BaseJson) elasticNodeMap.get(element.getId());
            boolean added;
            if (elasticElement == null) {
                added = true;
            } else {
                added = !elasticIds.contains(elasticElement.getElasticId());
            }
            boolean updated = false;
            Map<String, Object> rejected = new HashMap<>();
            if (!added) {

                // Check the overwrite flag - True = do not merge
                if (!overwriteJson) {
// See if element exists
//   check modified time is present in posted json and posted modified time < existing modified time, reject the posted element
//   merge existing json with posted json
//                    existingElasticNodes.indexOf(element.getId());
                    if (NodePostHelper.isUpdated(element, elasticElement, rejected)) {
                        updated = NodePostHelper.diffUpdateJson(element, elasticElement, rejected);
                    }
                } else {
                    updated = true;
                }
            }

// create new elastic id for all element json, update modified time, modifier (use dummy for now), set _projectId, _refId, _inRefIds
            element.setProjectId(DbContextHolder.getContext().getProjectId());
            element.setRefId(DbContextHolder.getContext().getBranchId());
            String elasticId = UUID.randomUUID().toString();
            element.setElasticId(elasticId);
            element.setCommitId(commitId);
//          Should match time on commit object and db table
            element.setModified(now.toString());
            element.setModifier("admin");

            if (added) {
                logger.debug("ELEMENT ADDED!");

                element.setCreator("coolkid"); //Only set on creation of new element
                element.setCreated(now.toString());

//                Commit object
                Map<String, Object> newObj = new HashMap<>();
                newObj.put(CommitJson.TYPE, NodeType.ELEMENT);
                newObj.put(BaseJson.ELASTICID, elasticId); // FIXME correct id?
                newObj.put(BaseJson.ID, element.getId());

                commitAdded.add(newObj);
                Node node = new Node();
                node.setSysmlId(element.getId());
                node.setElasticId(elasticId);
                node.setLastCommit(commitId);
                node.setInitialCommit(elasticId);
                node.setNodeType(NodeType.ELEMENT);
                toSave.add(node);
                response.getElements().add(element);
            } else if (updated) {
                logger.debug("ELEMENT UPDATED!");

//                Commit object
                Map<String, Object> newObj = new HashMap<>();
                newObj.put(CommitJson.PREVIOUS, elasticElement.getElasticId());
                newObj.put(CommitJson.TYPE, NodeType.ELEMENT);
                newObj.put(BaseJson.ELASTICID, elasticId);
                newObj.put(BaseJson.ID, element.getId());
                commitUpdated.add(newObj);

                Node node = (Node) exisitingNodeMap.get(element.getId());
                node.setElasticId(element.getElasticId());
                node.setLastCommit(commitId);
                node.setNodeType(NodeType.ELEMENT);
                node.setDeleted(false);
                toSave.add(node);
                response.getElements().add(element);
            } else {
                rejectedList.add(rejected);
                logger.debug("ELEMENT REJECTED!");
            }
        }
        return toSave;
    }

    public static boolean isUpdated(BaseJson element, Map<String, Object> existing,
        Map<String, Object> rejection) {
        if (existing == null) {
            return false;
        }

        boolean equiv = isEquivalent(element, existing);
        if (equiv) {
            rejection.put("message", "Is Equivalent");
            rejection.put("code", 304);
            rejection.put("element", element);
        }

        return !equiv;
    }

    private static boolean isEquivalent(Map<String, Object> map1, Map<String, Object> map2) {
        for (Map.Entry<String, Object> entry : map1.entrySet()) {
            if (!map2.containsKey(entry.getKey())) {
                return false;
            }
            Object value1 = entry.getValue();
            Object value2 = map2.get(entry.getKey());
            if (logger.isDebugEnabled()) {
                logger.debug("Value 1: " + value1);
                logger.debug("Value 2: " + value2);
            }
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
                        logger.debug("Is Equivalent: " + isEquivalent((List<Object>) value1,
                            (List<Object>) value2));
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

    public static boolean diffUpdateJson(BaseJson element, Map<String, Object> existing,
        Map<String, Object> rejection) {
        if (!element.getId().isEmpty() && existing.containsKey(BaseJson.ID)) {
            String jsonModified = element.getModified();
            Object existingModified = existing.get(BaseJson.MODIFIED);
            if (!jsonModified.isEmpty()) {
                try {
                    Date jsonModDate = df.parse(jsonModified);
                    Date existingModDate = df.parse(existingModified.toString());
                    if (jsonModDate.before(existingModDate)) {
                        if (logger.isDebugEnabled()) {
                            logger.debug("Conflict Detected");
                        }
                        rejection.put("message", "Conflict Detected");
                        rejection.put("code", 409);
                        rejection.put("element", element);
                        return false;
                    }
                } catch (ParseException e) {
                    if (logger.isDebugEnabled()) {
//                        logger.debug(String.format("%s", LogUtil.getStackTrace(e)));
                    }
                }
            }
            return mergeJson(element, existing);
        }
        return false;
    }

    private static boolean mergeJson(BaseJson partial, Map<String, Object> original) {
        if (original == null) {
            return false;
        }

        for (Map.Entry<String, Object> entry : original.entrySet()) {
            String attr = entry.getKey();
            if (!partial.containsKey(attr)) {
                partial.put(attr, original.get(attr));
            }
        }
        return true;
    }
}
