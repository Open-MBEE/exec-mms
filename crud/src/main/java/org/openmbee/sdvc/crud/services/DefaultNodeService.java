package org.openmbee.sdvc.crud.services;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openmbee.sdvc.crud.config.DbContextHolder;
import org.openmbee.sdvc.crud.controllers.elements.ElementJson;
import org.openmbee.sdvc.crud.controllers.elements.ElementsRequest;
import org.openmbee.sdvc.crud.controllers.elements.ElementsResponse;
import org.openmbee.sdvc.crud.domains.Commit;
import org.openmbee.sdvc.crud.domains.CommitType;
import org.openmbee.sdvc.crud.domains.Node;
import org.openmbee.sdvc.crud.repositories.commit.CommitDAO;
import org.openmbee.sdvc.crud.repositories.node.NodeDAO;
import org.openmbee.sdvc.crud.repositories.node.NodeElasticDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("defaultNodeService")
public class DefaultNodeService implements NodeService {

    protected final Logger logger = LogManager.getLogger(getClass());

    protected NodeDAO nodeRepository;
    protected CommitDAO commitRepository;
    protected CommitElasticDAO commitElacticRepository;
    protected NodeElasticDAO nodeElasticRepository;

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
        DbContextHolder.setContext(projectId, refId);
        ElementsResponse response = new ElementsResponse();
        response.put("extraKey", "blah");
        logger.info("source: " + req.getSource());
        for (ElementJson element : req.getElements()) {
            logger.info("Saving element with id: {}", element.getId());
            Node node = element.toNode();
            this.nodeRepository.save(node);
            response.getElements().add(element);
        }

        Commit commit = new Commit();
        commit.setBranchId(DbContextHolder.getContext().getBranchId());
        commit.setCommitType(CommitType.COMMIT);
        commit.setCreator("admin");
        commit.setElasticId("test");
        commit.setTimestamp(Instant.now());

        this.commitRepository.save(commit);
        this.commitElacticRepository.save();
        return response;
    }

}
