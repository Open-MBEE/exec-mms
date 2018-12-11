package org.openmbee.sdvc.crud.services;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openmbee.sdvc.crud.config.DbContextHolder;
import org.openmbee.sdvc.json.CommitJson;
import org.openmbee.sdvc.json.ElementJson;
import org.openmbee.sdvc.crud.controllers.elements.ElementsRequest;
import org.openmbee.sdvc.crud.controllers.elements.ElementsResponse;
import org.openmbee.sdvc.crud.domains.Commit;
import org.openmbee.sdvc.crud.domains.CommitType;
import org.openmbee.sdvc.crud.domains.Edge;
import org.openmbee.sdvc.crud.domains.Node;
import org.openmbee.sdvc.crud.repositories.commit.CommitDAO;
import org.openmbee.sdvc.crud.repositories.commit.CommitIndexDAO;
import org.openmbee.sdvc.crud.repositories.edge.EdgeDAO;
import org.openmbee.sdvc.crud.repositories.node.NodeDAO;
import org.openmbee.sdvc.crud.repositories.node.NodeIndexDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

@Service("defaultNodeService")
public class DefaultNodeService implements NodeService {

    protected final Logger logger = LogManager.getLogger(getClass());

    protected NodeDAO nodeRepository;
    protected CommitDAO commitRepository;
    protected NodeIndexDAO nodeIndex;
    //to save to this use base json classes
    protected CommitIndexDAO commitIndex;
    protected EdgeDAO edgeRepository;
    protected NodePostHelper nodePostHelper;

    @Autowired
    public void setNodeRepository(NodeDAO nodeRepository) {
        this.nodeRepository = nodeRepository;
    }

    @Autowired
    public void setCommitRepository(CommitDAO commitRepository) {
        this.commitRepository = commitRepository;
    }

    @Autowired
    public void setNodeIndex(NodeIndexDAO nodeIndex) {
        this.nodeIndex = nodeIndex;
    }

    @Autowired
    public void setCommitIndex(CommitIndexDAO commitIndex) {
        this.commitIndex = commitIndex;
    }

    @Autowired
    public void setEdgeRepository(EdgeDAO edgeRepository) {
        this.edgeRepository = edgeRepository;
    }

    @Autowired
    public void setNodePostHelper(NodePostHelper nodePostHelper) {
        this.nodePostHelper = nodePostHelper;
    }

    @Override
    public ElementsResponse get(String projectId, String refId, String id,
        Map<String, String> params) {

        DbContextHolder.setContext(projectId, refId);
        logger.info("params: " + params);
        if (id != null) {
            logger.debug("ElementId given: ", id);
            Node node = nodeRepository.findByNodeId(id);
            ElementJson e = new ElementJson();
            e.setId(node.getNodeId());
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

        DbContextHolder.setContext(projectId, refId);
        boolean overwriteJson = Boolean.parseBoolean(params.get("overwrite"));
        Instant now = Instant.now();

        CommitJson cmjs = new CommitJson();
        cmjs.setCreator("admin");
        cmjs.setComment(req.getComment());
        cmjs.setSource(req.getSource());
        cmjs.setRefId(refId);
        cmjs.setProjectId(projectId);

        List<Map> rejectedList = new ArrayList<>();
        Map<String, ElementJson> responses = new HashMap<>();
        Set<String> oldElasticIds = new HashSet<>();
        Map<String, Node> toSave = nodePostHelper
            .processPostJson(req.getElements(), overwriteJson, now, cmjs, responses, rejectedList,
                this, oldElasticIds);

        try {
            commitChanges(toSave, responses, cmjs, now, oldElasticIds);
        } catch (Exception e) {
            //TODO db transaction
        }
        ElementsResponse response = new ElementsResponse();
        response.getElements().addAll(responses.values());
        response.put("rejected", rejectedList);
        return response;
    }

    protected void commitChanges(Map<String, Node> nodes, Map<String, ElementJson> json,
        CommitJson cmjs, Instant now, Set<String> oldIndexIds) {
        if (!nodes.isEmpty()) {
            this.nodeIndex.indexAll(json.values());
            this.nodeRepository.saveAll(new ArrayList<>(nodes.values()));

            List<Edge> edges = nodePostHelper.getEdgesToSave(nodes, json, this);
            this.edgeRepository.saveAll(edges);

            //TODO update old elastic ids to remove ref from inRefIds
//            DB Commit
            Commit commit = new Commit();
            commit.setBranchId(cmjs.getRefId());
            commit.setCommitType(CommitType.COMMIT);
            commit.setCreator(cmjs.getCreator());
            commit.setIndexId(cmjs.getId());
            commit.setTimestamp(now);
            commit.setComment(cmjs.getComment());

            this.commitIndex.index(cmjs);
            this.commitRepository.save(commit);
        }
    }

    @Override
    public void extraProcessPostedElement(ElementJson element, Node node,
        Set<String> oldElasticIds, CommitJson cmjs, Instant now, Map<String, Node> toSave,
        Map<String, ElementJson> response) {
    }

    @Override
    public Map<Integer, List<Pair<String, String>>> getEdgeInfo(Collection<ElementJson> elements) {
        return new HashMap<>();
    }
}
