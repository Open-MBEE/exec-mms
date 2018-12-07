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
        List<Map<String, Object>> rejectedList = new ArrayList<>();
        List<Node> toSave = NodePostHelper.processPostJson(req.getElements(), elasticNodeMap, elasticIds, rejectedList,
            overwriteJson, now, commitAdded, commitUpdated, commitDeleted, commitId, response, exisitingNodeMap);

        if (toSave.isEmpty()) {
            this.nodeRepository.saveAll(toSave);

//            DB Commit
            Commit commit = new Commit();
            commit.setBranchId(DbContextHolder.getContext().getBranchId());
            commit.setCommitType(CommitType.COMMIT);
            commit.setCreator("admin");
            commit.setElasticId(commitId);
            commit.setTimestamp(now);

            this.commitRepository.save(commit);

//            Index Commit
            cmjs.setCreator("admin");
            cmjs.setComment("this is a commit");
            cmjs.setAdded(commitAdded);
            cmjs.setDeleted(commitDeleted);
            cmjs.setUpdated(commitUpdated);
            cmjs.setSource(source);
            cmjs.setElasticId(commitId);
            cmjs.setId(commitId);

//            this.commitElasticRepository.save(cmjs);
        }
        response.put("rejected", rejectedList);

        return response;
    }





}
