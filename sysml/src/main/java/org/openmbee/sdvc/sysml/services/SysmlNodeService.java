package org.openmbee.sdvc.sysml.services;

import org.openmbee.sdvc.crud.controllers.elements.ElementsRequest;
import org.openmbee.sdvc.crud.controllers.elements.ElementsResponse;
import org.openmbee.sdvc.crud.repositories.edge.EdgeDAO;
import org.openmbee.sdvc.crud.services.DefaultNodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service("sysmlNodeService")
public class SysmlNodeService extends DefaultNodeService {

    private EdgeDAO edgeRepository;

    private CameoHelper cameoHelper;

    @Autowired
    public void setEdgeRepository(EdgeDAO edgeRepository) {
        this.edgeRepository = edgeRepository;
    }

    @Autowired
    public void setCameoHelper(CameoHelper cameoHelper) {
        this.cameoHelper = cameoHelper;
    }

    @Override
    public ElementsResponse get(String projectId, String refId, String id, Map<String, String> params) {
        //need to use mount search to accommodate depth param and project mounts
        //add childviews and extended info
        return super.get(projectId, refId, id, params);
    }

    @Override
    public ElementsResponse post(String projectId, String refId, ElementsRequest req, Map<String, String> params) {
        //TODO use default node service or post node helper to process input elements, need list of merged/rejected elements and nodes, added, updated
        //process childviews, edges, add to deleted/added/updated
        //save nodes
        //delete existing edges, save new edges
        return super.post(projectId, refId, req, params);
    }

}
