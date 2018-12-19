package org.openmbee.sdvc.crud.services;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.client.RestHighLevelClient;
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
import org.springframework.beans.factory.annotation.Qualifier;
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
    protected NodeGetHelper nodeGetHelper;
    protected NodePostHelper nodePostHelper;
    protected NodeDeleteHelper nodeDeleteHelper;


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

    @Autowired
    public void setNodeDeleteHelper(NodeDeleteHelper nodeDeleteHelper) {
        this.nodeDeleteHelper = nodeDeleteHelper;
    }

    @Autowired
    public void setNodeGetHelper(NodeGetHelper nodeGetHelper) {
        this.nodeGetHelper = nodeGetHelper;
    }

    @Override
    public ElementsResponse get(String projectId, String refId, String id,
        Map<String, String> params) {

        DbContextHolder.setContext(projectId, refId);
        if (id != null) {
            logger.debug("ElementId given: ", id);

            ElementJson json = new ElementJson();
            json.setId(id);
            ElementsRequest req = new ElementsRequest();
            List<ElementJson> list = new ArrayList<>();
            list.add(json);
            req.setElements(list);
            return get(req, params);

        } else {
//            If no id is provided, return all
            logger.debug("No ElementId given");

            ElementsResponse response = new ElementsResponse();
            response.getElements().addAll(nodeGetHelper.processGetAll().values());
            return response;
        }
    }

    public ElementsResponse get(ElementsRequest req, Map<String, String> params) {

//        params commit it get element at a commit id
//        find a specific element at a commit
//        commit DB and if element was actually edited at that commit - get element
//        otherwise get timestamp of commit - find element before timestamp
//        get all the commits in ref and search elastic for all the elements (for that specific) sorted by time and check
//        check if current state of element and if timestamp is less then pass that version
        logger.info("params: " + params);

        NodeGetInfo info = nodeGetHelper.processGetJson(req.getElements());

        ElementsResponse response = new ElementsResponse();
        response.getElements().addAll(info.getExistingElementMap().values());
        response.put("rejected", info.getRejected());
        return response;
    }

    @Override
    public ElementsResponse post(String projectId, String refId, ElementsRequest req,
        Map<String, String> params) {

        DbContextHolder.setContext(projectId, refId);
        boolean overwriteJson = Boolean.parseBoolean(params.get("overwrite"));

        CommitJson cmjs = new CommitJson();
        cmjs.setCreator("admin");
        cmjs.setComment(req.getComment());
        cmjs.setSource(req.getSource());
        cmjs.setRefId(refId);
        cmjs.setProjectId(projectId);

        NodeChangeInfo info = nodePostHelper
            .processPostJson(req.getElements(), overwriteJson, cmjs, this);

        try {
            commitChanges(info);
        } catch (Exception e) {
            //TODO db transaction
        }
        ElementsResponse response = new ElementsResponse();
        response.getElements().addAll(info.getUpdatedMap().values());
        response.put("rejected", info.getRejected());
        return response;
    }

    protected void commitChanges(NodeChangeInfo info) throws IOException {
        Map<String, Node> nodes = info.getToSaveNodeMap();
        Map<String, ElementJson> json = info.getUpdatedMap();
        CommitJson cmjs = info.getCommitJson();
        Instant now = info.getNow();

        if (!nodes.isEmpty()) {
            this.nodeRepository.saveAll(new ArrayList<>(nodes.values()));
            if (json != null && !json.isEmpty()) {
                this.nodeIndex.indexAll(json.values());
                List<Edge> edges = nodePostHelper.getEdgesToSave(info);
                this.edgeRepository.saveAll(edges);
            }

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
    public void extraProcessPostedElement(ElementJson element, Node node, NodeChangeInfo info) {
    }

    @Override
    public void extraProcessDeletedElement(ElementJson element, Node node, NodeChangeInfo info) {
    }

    @Override
    public ElementsResponse delete(String projectId, String refId, String id) {
        ElementJson json = new ElementJson();
        json.setId(id);
        ElementsRequest req = new ElementsRequest();
        List<ElementJson> list = new ArrayList<>();
        list.add(json);
        req.setElements(list);
        return delete(projectId, refId, req);
    }

    @Override
    public ElementsResponse delete(String projectId, String refId, ElementsRequest req) {
        DbContextHolder.setContext(projectId, refId);

        CommitJson cmjs = new CommitJson();
        cmjs.setCreator("admin");
        cmjs.setComment(req.getComment());
        cmjs.setSource(req.getSource());
        cmjs.setRefId(refId);
        cmjs.setProjectId(projectId);
        NodeChangeInfo info = nodeDeleteHelper.processDeleteJson(req.getElements(), cmjs, this);

        try {
            commitChanges(info);
        } catch (Exception e) {
            //TODO db transaction
        }
        ElementsResponse response = new ElementsResponse();
        response.getElements().addAll(info.getDeletedMap().values());
        response.put("rejected", info.getRejected());
        return response;
    }
}
