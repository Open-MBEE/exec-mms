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
import javax.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openmbee.sdvc.crud.config.DbContextHolder;
import org.openmbee.sdvc.crud.controllers.BaseJson;
import org.openmbee.sdvc.crud.controllers.ErrorResponse;
import org.openmbee.sdvc.crud.controllers.commits.CommitJson;
import org.openmbee.sdvc.crud.controllers.elements.ElementJson;
import org.openmbee.sdvc.crud.controllers.elements.ElementsRequest;
import org.openmbee.sdvc.crud.controllers.elements.ElementsResponse;
import org.openmbee.sdvc.crud.domains.Commit;
import org.openmbee.sdvc.crud.domains.CommitType;
import org.openmbee.sdvc.crud.domains.Node;
import org.openmbee.sdvc.crud.domains.NodeType;
import org.openmbee.sdvc.crud.repositories.commit.CommitDAO;
import org.openmbee.sdvc.crud.repositories.commit.CommitElasticDAO;
import org.openmbee.sdvc.crud.repositories.node.NodeDAO;
import org.openmbee.sdvc.crud.repositories.node.NodeElasticDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("defaultNodeService")
public class DefaultNodeService implements NodeService {

    protected final Logger logger = LogManager.getLogger(getClass());

    protected NodeDAO nodeRepository;
    protected CommitDAO commitRepository;
    protected NodeElasticDAO nodeElasticRepository;
    //to save to this use base json classes
    protected CommitElasticDAO commitElasticRepository;

    public static SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

    @Autowired
    public void setNodeRepository(NodeDAO nodeRepository) {
        this.nodeRepository = nodeRepository;
    }

    @Autowired
    public void setCommitRepository(CommitDAO commitRepository) {
        this.commitRepository = commitRepository;
    }

    @Autowired
    public void setNodeElasticRepository(NodeElasticDAO nodeElasticRepository) {
        this.nodeElasticRepository = nodeElasticRepository;
    }

    @Override
    public ElementsResponse get(String projectId, String refId, String id,
        Map<String, String> params) {
        DbContextHolder.setContext(projectId, refId);
        logger.info("params: " + params);
        if (id != null) {
            logger.debug("ElementId given: ", id);
            Node node = nodeRepository.findBySysmlId(id);
            ElementJson e = new ElementJson();
            e.setId(node.getSysmlId());
            //set other stuff
            ElementsResponse res = new ElementsResponse();
            List<ElementJson> list = new ArrayList<>();
            list.add(e);
            res.setElements(list);
            return res;
        } else {
            logger.debug("No ElementId given");
            List<Node> nodes = nodeRepository.findAll();
            //return ResponseEntity.ok(new ElementsResponse(nodes));
        }
        return null;
    }

    @Override
    public ElementsResponse post(String projectId, String refId, ElementsRequest req,
        Map<String, String> params) {

        String source = req.getSource();
        logger.info("source: " + source);
        DbContextHolder.setContext(projectId, refId);
        boolean overwriteJson = Boolean.parseBoolean(params.get("overwrite"));
        Instant now = Instant.now();
        String commitId = UUID.randomUUID().toString();
        ElementsResponse response = new ElementsResponse();
        CommitJson cmjs = new CommitJson();

        List<Map<String, Object>> commitAdded = new ArrayList<>();
        List<Map<String, Object>> commitUpdated = new ArrayList<>();
        List<Map<String, Object>> commitDeleted = new ArrayList<>();

        // get element data from elastic
        Set<String> elasticIds = new HashSet<>();
        Map<String, Object> reqElementMap = NodePostHelper.convertToMap(req.getElements());
        Set<String> keys = reqElementMap.keySet();
        List<String> sysmlids = new ArrayList<>(keys);
        List<Node> existingNodes = nodeRepository.findAllBySysmlIds(sysmlids);
        Map<String, Object> exisitingNodeMap = new HashMap<>();

        for (Node node : existingNodes) {
            logger.info("Get element with id: {}", node.getId());
            elasticIds.add(node.getElasticId());
            exisitingNodeMap.put(node.getSysmlId(), node);
        }
        // bulk get existing elements in elastic
        List<Map<String, Object>> existingElasticNodes = nodeElasticRepository.findByElasticIds(elasticIds);
        Map<String, Object> elasticNodeMap = NodePostHelper.convertListToMap(existingElasticNodes);

        // Logic for update/add
        for (ElementJson element : req.getElements()) {

            BaseJson elasticElement = (BaseJson) elasticNodeMap.get(element.getId());
            boolean added;
            if (elasticElement == null) {
                added = true;
            } else{
                added = !elasticIds.contains(elasticElement.getElasticId());
            }
            boolean updated = false;
            Map<Integer, String> rejected = new HashMap<>();
            if (!added) {

                // Check the overwrite flag - True = do not merge
                if (!overwriteJson) {
// See if element exists
//   check modified time is present in posted json and posted modified time < existing modified time, reject the posted element
//   merge existing json with posted json
//                    existingElasticNodes.indexOf(element.getId());
                    if (NodePostHelper.isUpdated(element, elasticElement, rejected)) {
                        updated = diffUpdateJson(element, elasticElement, rejected);
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
//            element.setCommitId????
//          Should match time on commit object and db table
            element.setModified(now.toString());
            element.setModifier("admin");

            if (added) {
                logger.debug("ELEMENT ADDED!");

                element.setCreator("coolkid"); //Only set on creation of new element
                element.setCreated(now.toString());
//                addedElements.add(o);

//                Commit object
                Map<String, Object> newObj = new HashMap<>();
                newObj.put(CommitJson.TYPE, NodeType.ELEMENT);
                newObj.put(BaseJson.ELASTICID, elasticId); // FIXME correct id?
                newObj.put(BaseJson.ID, element.getId());

                commitAdded.add(newObj);
//                newElements.add(o);
                Node node = element.toNode();
                node.setLastCommit(commitId);
                node.setInitialCommit(elasticId);
                node.setNodeType(NodeType.ELEMENT);
                this.nodeRepository.save(node);
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
                this.nodeRepository.save(node);
                response.getElements().add(element);
            } else {
                for (Map.Entry<Integer, String> message : rejected.entrySet()) {
                    ErrorResponse errorPayload = new ErrorResponse();
                    errorPayload.setCode(message.getKey());
                    errorPayload.setError(message.getValue());
//                    errorPayload.add("element", o);
//                    errorPayload.addProperty("severity", Sjm.WARN);
//                    rejectedElements.add(errorPayload);
                }
                logger.debug("ELEMENT REJECTED!");
            }
        }

//        response.put("addedElements", addedElements);
//        response.put("updatedElements", updatedElements);
//        response.put("newElements", newElements);
//        response.put("deletedElements", deletedElements);
//        response.put("rejectedElements", rejectedElements);

//        TODO do not make a commit if nothing has been updated
//        do we still want to push anything to commit table? i assume no
        Commit commit = new Commit();
        commit.setBranchId(DbContextHolder.getContext().getBranchId());
        commit.setCommitType(CommitType.COMMIT);
        commit.setCreator("admin");
        commit.setElasticId(commitId);
        commit.setTimestamp(now);

        this.commitRepository.save(commit);

        cmjs.setCreator("admin");
        cmjs.setComment("this is a commit");
        cmjs.setAdded(commitAdded);
        cmjs.setDeleted(commitDeleted);
        cmjs.setUpdated(commitUpdated);
        cmjs.setSource(source);
        cmjs.setElasticId(commitId);
        cmjs.setId(commitId);

//        this.commitElasticRepository.save(cmjs);

        response.put("commit", cmjs);

        return response;
    }


    // create new elastic id for all element json, update modified time, modifier (use dummy for now), set _projectId, _refId, _inRefIds
//    private Node processPostJson(List<ElementJson> elements, Map<String, Object> existingElt, Map<Integer, String> rejected) {
//
//
//
//    }

    protected boolean isUpdated(BaseJson element, Map<String, Object> existing, Map<Integer, String> rejected) {
        if (existing == null) {
            return false;
        }

        if (logger.isDebugEnabled()) {
            logger.debug("New Element: " + element);
            logger.debug("Old Element: " + existing);
        }

//        Map<String, Object> newElement = toMap(element);
//        Map<String, Object> oldElement = toMap(existing);

//        boolean equiv = isEquivalent(newElement, oldElement);
//
//        if (equiv) {
//            rejection.put(HttpServletResponse.SC_NOT_MODIFIED, "Is Equivalent");
//        }
//
//        return !equiv;
        return true;
    }

    private boolean diffUpdateJson(BaseJson element, Map<String, Object> existing, Map<Integer, String> rejection) {
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
                        rejection.put(HttpServletResponse.SC_CONFLICT, "Conflict Detected");
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

    private boolean mergeJson(BaseJson partial, Map<String, Object> original) {
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
