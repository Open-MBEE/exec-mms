package org.openmbee.sdvc.sysml.services;

import org.openmbee.sdvc.crud.config.DbContextHolder;
import org.openmbee.sdvc.crud.controllers.elements.ElementJson;
import org.openmbee.sdvc.crud.controllers.elements.ElementsRequest;
import org.openmbee.sdvc.crud.controllers.elements.ElementsResponse;
import org.openmbee.sdvc.crud.domains.Commit;
import org.openmbee.sdvc.crud.domains.CommitType;
import org.openmbee.sdvc.crud.domains.Node;
import org.openmbee.sdvc.crud.repositories.edge.EdgeDAO;
import org.openmbee.sdvc.crud.services.DefaultNodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service("sysmlNodeService")
public class SysmlNodeService extends DefaultNodeService {

    private EdgeDAO edgeRepository;

    @Autowired
    public void setEdgeRepository(EdgeDAO edgeRepository) { this.edgeRepository = edgeRepository; }

    @Override
    public ElementsResponse get(String projectId, String refId, String id, Map<String, String> params) {
        return super.get(projectId, refId, id, params);
    }

    @Override
    public ElementsResponse post(String projectId, String refId, ElementsRequest req, Map<String, String> params) {
        return super.post(projectId, refId, req, params);
    }

}
