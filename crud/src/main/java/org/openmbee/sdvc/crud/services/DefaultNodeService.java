package org.openmbee.sdvc.crud.services;

import java.sql.SQLException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openmbee.sdvc.core.services.NodeChangeInfo;
import org.openmbee.sdvc.core.services.NodeGetInfo;
import org.openmbee.sdvc.core.services.NodeService;
import org.openmbee.sdvc.core.config.DbContextHolder;
import org.openmbee.sdvc.core.objects.ElementsRequest;
import org.openmbee.sdvc.core.objects.ElementsResponse;
import org.openmbee.sdvc.crud.exceptions.InternalErrorException;
import org.openmbee.sdvc.data.domains.Commit;
import org.openmbee.sdvc.data.domains.CommitType;
import org.openmbee.sdvc.data.domains.Edge;
import org.openmbee.sdvc.data.domains.Node;
import org.openmbee.sdvc.rdb.repositories.commit.CommitDAO;
import org.openmbee.sdvc.core.services.CommitIndexDAO;
import org.openmbee.sdvc.rdb.repositories.edge.EdgeDAO;
import org.openmbee.sdvc.rdb.repositories.node.NodeDAO;
import org.openmbee.sdvc.core.services.NodeIndexDAO;
import org.openmbee.sdvc.json.CommitJson;
import org.openmbee.sdvc.json.ElementJson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;


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
    public ElementsResponse read(String projectId, String refId, String id,
        Map<String, String> params) {

        if (id != null) {
            logger.debug("ElementId given: ", id);

            ElementsRequest req = buildRequest(id);
            return read(projectId, refId, req, params);

        } else {
            // If no id is provided, return all
            logger.debug("No ElementId given");
            DbContextHolder.setContext(projectId, refId);

            ElementsResponse response = new ElementsResponse();
            String commitId = params.getOrDefault("commitId", null);
            response.getElements().addAll(nodeGetHelper.processGetAll(commitId));
            return response;
        }
    }

    @Override
    public ElementsResponse read(String projectId, String refId, ElementsRequest req,
        Map<String, String> params) {

        String commitId = params.getOrDefault("commitId", null);
        DbContextHolder.setContext(projectId, refId);
        logger.info("params: " + params);

        NodeGetInfo info = nodeGetHelper.processGetJson(req.getElements(), commitId);

        ElementsResponse response = new ElementsResponse();
        response.getElements().addAll(info.getActiveElementMap().values());
        response.put("rejected", info.getRejected());
        return response;
    }

    @Override
    public ElementsResponse createOrUpdate(String projectId, String refId, ElementsRequest req,
        Map<String, String> params) {

        DbContextHolder.setContext(projectId, refId);
        boolean overwriteJson = Boolean.parseBoolean(params.get("overwrite"));

        NodeChangeInfo info = nodePostHelper
            .processPostJson(req.getElements(), overwriteJson,
                createCommit("admin", refId, projectId, req), this);

        commitChanges(info);

        ElementsResponse response = new ElementsResponse();
        response.getElements().addAll(info.getUpdatedMap().values());
        response.setRejected(info.getRejected());
        return response;
    }

    @Transactional
    protected void commitChanges(NodeChangeInfo info) {
        //TODO: Test rollback on IndexDAO failure
        TransactionDefinition def = new DefaultTransactionDefinition();
        TransactionStatus status = this.nodeRepository.getTransactionManager().getTransaction(def);

        Map<String, Node> nodes = info.getToSaveNodeMap();
        Map<String, ElementJson> json = info.getUpdatedMap();
        CommitJson cmjs = info.getCommitJson();
        Instant now = info.getNow();
        List<Edge> edges = null;
        if (!nodes.isEmpty()) {
            try {
                this.nodeRepository.saveAll(new ArrayList<>(nodes.values()));
                if (json != null && !json.isEmpty()) {
                    //edges needed nodes to save first in order to get id
                    edges = nodePostHelper.getEdgesToSave(info);
                    this.nodeIndex.indexAll(json.values());
                    this.edgeRepository.saveAll(edges);
                }
                this.nodeRepository.getTransactionManager().commit(status);
            } catch (SQLException e) {
                logger.error("commitChanges error: ", e);
                this.nodeRepository.getTransactionManager().rollback(status);
                throw new InternalErrorException("Error committing transaction");
            }

            this.nodeIndex.removeFromRef(info.getOldIndexIds());

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
        ElementsRequest req = buildRequest(id);
        return delete(projectId, refId, req);
    }

    protected ElementsRequest buildRequest(String id) {
        ElementJson json = new ElementJson();
        json.setId(id);
        ElementsRequest req = new ElementsRequest();
        List<ElementJson> list = new ArrayList<>();
        list.add(json);
        req.setElements(list);
        return req;
    }

    @Override
    public ElementsResponse delete(String projectId, String refId, ElementsRequest req) {
        DbContextHolder.setContext(projectId, refId);

        NodeChangeInfo info = nodeDeleteHelper
            .processDeleteJson(req.getElements(), createCommit("admin", refId, projectId, req),
                this);
        ElementsResponse response = new ElementsResponse();

        commitChanges(info);

        response.getElements().addAll(info.getDeletedMap().values());
        response.setRejected(info.getRejected());
        return response;
    }

    private CommitJson createCommit(String creator, String refId, String projectId,
        ElementsRequest req) {
        CommitJson cmjs = new CommitJson();
        cmjs.setCreator(creator);
        cmjs.setComment(req.getComment());
        cmjs.setSource(req.getSource());
        cmjs.setRefId(refId);
        cmjs.setProjectId(projectId);
        return cmjs;
    }
}
