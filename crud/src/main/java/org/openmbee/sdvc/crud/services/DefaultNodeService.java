package org.openmbee.sdvc.crud.services;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openmbee.sdvc.crud.controllers.elements.ElementsPostRequest;
import org.openmbee.sdvc.crud.controllers.elements.ElementsResponse;
import org.openmbee.sdvc.crud.repositories.commit.CommitDAO;
import org.openmbee.sdvc.crud.repositories.node.NodeDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service("defaultNodeService")
public class DefaultNodeService implements NodeService {
    protected final Logger logger = LogManager.getLogger(getClass());

    protected NodeDAO nodeRepository;
    protected CommitDAO commitRepository;

    @Autowired
    public void setNodeRepository(NodeDAO nodeRepository) {
        this.nodeRepository = nodeRepository;
    }

    @Autowired
    public void setCommitRepository(CommitDAO commitRepository) {
        this.commitRepository = commitRepository;
    }

    @Override
    public ElementsResponse get(String projectId, String refId, String id, Map<String, String> params) {
        return null;
    }

    @Override
    public ElementsResponse post(String projectId, String refId, ElementsPostRequest req, Map<String, String> params) {
        return null;
    }

}
