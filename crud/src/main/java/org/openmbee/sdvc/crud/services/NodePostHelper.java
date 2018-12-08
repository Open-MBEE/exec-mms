package org.openmbee.sdvc.crud.services;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
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
import org.openmbee.sdvc.crud.domains.Edge;
import org.openmbee.sdvc.crud.domains.EdgeType;
import org.openmbee.sdvc.crud.domains.Node;
import org.openmbee.sdvc.crud.domains.NodeType;
import org.openmbee.sdvc.crud.repositories.node.NodeDAO;
import org.openmbee.sdvc.crud.repositories.node.NodeElasticDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;

@Component
public class NodePostHelper {

    protected static final Logger logger = LogManager.getLogger(NodePostHelper.class);
    public static SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
    protected NodeDAO nodeRepository;
    protected NodeElasticDAO nodeElasticRepository;

    public static void processElementAdded(ElementJson e, Node n, CommitJson cmjs) {
        processElementAddedOrUpdated(e, n, cmjs);

        e.setCreator(cmjs.getCreator()); //Only set on creation of new element
        e.setCreated(cmjs.getCreated());

        Map<String, Object> newObj = new HashMap<>();
        newObj.put(CommitJson.TYPE, "Element");
        newObj.put(BaseJson.ELASTICID, e.getElasticId());
        newObj.put(BaseJson.ID, e.getId());
        cmjs.getAdded().add(newObj);

        n.setSysmlId(e.getId());
        n.setElasticId(e.getElasticId());
        n.setLastCommit(cmjs.getId());
        n.setInitialCommit(e.getElasticId());
        n.setNodeType(NodeType.ELEMENT);
        n.setDeleted(false);
    }

    public static void processElementUpdated(ElementJson e, Node n, CommitJson cmjs) {
        processElementAddedOrUpdated(e, n, cmjs);

        Map<String, Object> newObj = new HashMap<>();
        newObj.put(CommitJson.PREVIOUS, n.getElasticId());
        newObj.put(CommitJson.TYPE, "Element");
        newObj.put(BaseJson.ELASTICID, e.getElasticId());
        newObj.put(BaseJson.ID, e.getId());
        cmjs.getUpdated().add(newObj);

        n.setElasticId(e.getElasticId());
        n.setLastCommit(cmjs.getId());
        n.setNodeType(NodeType.ELEMENT);
        n.setDeleted(false);
    }

    public static void processElementDeleted(ElementJson e, Node n, CommitJson cmjs) {
        Map<String, Object> newObj = new HashMap<>();
        newObj.put(CommitJson.PREVIOUS, n.getElasticId());
        newObj.put(CommitJson.TYPE, "Element");
        newObj.put(BaseJson.ID, e.getId());
        cmjs.getDeleted().add(newObj);

        n.setDeleted(true);
    }

    public static void processElementAddedOrUpdated(ElementJson e, Node n, CommitJson cmjs) {
        e.setProjectId(DbContextHolder.getContext().getProjectId());
        e.setRefId(DbContextHolder.getContext().getBranchId());
        String elasticId = UUID.randomUUID().toString();
        e.setElasticId(elasticId);
        e.setCommitId(cmjs.getId());
        e.setModified(cmjs.getCreated());
        e.setModifier(cmjs.getCreator());
    }

    public static boolean isUpdated(BaseJson element, Map<String, Object> existing,
        Map<String, Object> rejection) {

        boolean equiv = Helper.isEquivalent(element, existing);
        if (equiv) {
            rejection.put("message", "Is Equivalent");
            rejection.put("code", 304);
            rejection.put("element", element);
        }
        return !equiv;
    }

    public static boolean diffUpdateJson(BaseJson element, Map<String, Object> existing,
        Map<String, Object> rejection) {

        String jsonModified = element.getModified();
        Object existingModified = existing.get(BaseJson.MODIFIED);
        if (jsonModified != null && !jsonModified.isEmpty()) {
            try {
                Date jsonModDate = df.parse(jsonModified);
                Date existingModDate = df.parse(existingModified.toString());
                if (jsonModDate.before(existingModDate)) {
                    rejection.put("message", "Conflict Detected");
                    rejection.put("code", 409);
                    rejection.put("element", element);
                    return false;
                }
            } catch (ParseException e) {
                logger.info("date parse exception:" + jsonModified + " " + existingModified);
            }
        }
        mergeJson(element, existing);
        return true;
    }

    private static void mergeJson(BaseJson partial, Map<String, Object> original) {
        for (Map.Entry<String, Object> entry : original.entrySet()) {
            String attr = entry.getKey();
            if (!partial.containsKey(attr)) {
                partial.put(attr, original.get(attr));
            }
        }
    }

    @Autowired
    public void setNodeRepository(NodeDAO nodeRepository) {
        this.nodeRepository = nodeRepository;
    }

    @Autowired
    public void setNodeElasticRepository(NodeElasticDAO nodeElasticRepository) {
        this.nodeElasticRepository = nodeElasticRepository;
    }

    // create new elastic id for all element json, update modified time, modifier (use dummy for now), set _projectId, _refId, _inRefIds
    public Map<String, Node> processPostJson(List<ElementJson> elements, boolean overwriteJson,
        Instant now, CommitJson cmjs, Map<String, ElementJson> response, List<Map> rejectedList,
        NodeService service) {

        Set<String> elasticIds = new HashSet<>();
        Map<String, ElementJson> reqElementMap = (Map<String, ElementJson>) Helper
            .convertJsonToMap(elements);
        List<Node> existingNodes = nodeRepository.findAllBySysmlIds(reqElementMap.keySet());
        Map<String, Node> existingNodeMap = new HashMap<>();
        Set<String> oldElasticIds = new HashSet<>();
        for (Node node : existingNodes) {
            logger.info("Got element with id: {}", node.getId());
            elasticIds.add(node.getElasticId());
            existingNodeMap.put(node.getSysmlId(), node);
        }
        // bulk get existing elements in elastic
        List<Map<String, Object>> existingElasticNodes = nodeElasticRepository
            .findByElasticIds(elasticIds);
        Map<String, Object> elasticNodeMap = Helper
            .convertToMap(existingElasticNodes, ElementJson.ID);

        Map<String, Node> toSave = new HashMap<>();
        cmjs.setId(UUID.randomUUID().toString());
        cmjs.setElasticId(cmjs.getId());
        cmjs.setCreated(now.toString());
        cmjs.setAdded(new ArrayList<>());
        cmjs.setDeleted(new ArrayList<>());
        cmjs.setUpdated(new ArrayList<>());

        // Logic for update/add
        for (ElementJson element : elements) {
            if (element == null) {
                continue;
            }
            Map<String, Object> rejected = new HashMap<>();
            boolean added = false;
            boolean updated = false;
            if (element.getId() == null || element.getId().isEmpty()) {
                rejected.put("message", "missing id");
                rejected.put("code", 400);
                rejected.put("element", element);
            } else {
                Map<String, Object> elasticElement = (Map<String, Object>) elasticNodeMap
                    .get(element.getId());

                if (!existingNodeMap.containsKey(element.getId())) {
                    added = true;
                } else if (elasticElement == null) {
                    continue; //TODO this should be an error - the db has entry but elastic doesn't, reject 500?
                }

                if (!added) {
                    if (!overwriteJson) {
                        if (NodePostHelper.isUpdated(element, elasticElement, rejected)) {
                            updated = diffUpdateJson(element, elasticElement, rejected);
                        }
                    } else {
                        updated = true;
                    }
                }
            }

// create new elastic id for all element json, update modified time, modifier (use dummy for now), set _projectId, _refId, _inRefIds
            if (added) {
                Node node = new Node();
                processElementAdded(element, node, cmjs);
                toSave.put(node.getSysmlId(), node);
                response.put(element.getId(), element);
                service.extraProcessPostedElement(element, node, oldElasticIds, cmjs, now, toSave,
                    response);
            } else if (updated) {
                Node node = existingNodeMap.get(element.getId());
                oldElasticIds.add(node.getElasticId());
                processElementUpdated(element, node, cmjs);
                toSave.put(node.getSysmlId(), node);
                response.put(element.getId(), element);
                service.extraProcessPostedElement(element, node, oldElasticIds, cmjs, now, toSave,
                    response);
            } else {
                rejectedList.add(rejected);
            }
        }
        return toSave;
    }

    public List<Edge> getEdgesToSave(Map<String, Node> nodes, Map<String, ElementJson> json,
        NodeService service) {

        Set<String> toFind = new HashSet<>();
        Map<EdgeType, List<Pair<String, String>>> edges = service.getEdgeInfo(json.values());
        for (Map.Entry<EdgeType, List<Pair<String, String>>> entry : edges.entrySet()) {
            for (Pair<String, String> pair : entry.getValue()) {
                toFind.add(pair.getFirst());
                toFind.add(pair.getSecond());
            }
        }
        toFind.removeAll(nodes.keySet());
        Map<String, Node> edgeNodes = Helper
            .convertNodesToMap(this.nodeRepository.findAllBySysmlIds(toFind));
        edgeNodes.putAll(nodes);
        List<Edge> res = new ArrayList<>();
        for (Map.Entry<EdgeType, List<Pair<String, String>>> entry : edges.entrySet()) {
            for (Pair<String, String> pair : entry.getValue()) {
                Node parent = edgeNodes.get(pair.getFirst());
                Node child = edgeNodes.get(pair.getSecond());
                if (parent == null || child == null) {
                    continue; //TODO error or specific remedy?
                }
                Edge e = new Edge();
                e.setParent(parent);
                e.setChild(child);
                e.setEdgeType(entry.getKey());
                res.add(e);
            }
        }
        return res;
    }
}
