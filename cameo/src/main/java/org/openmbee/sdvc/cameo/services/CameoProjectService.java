package org.openmbee.sdvc.cameo.services;

import org.openmbee.sdvc.core.config.Constants;
import org.openmbee.sdvc.core.objects.ElementsRequest;
import org.openmbee.sdvc.core.services.NodeService;
import org.openmbee.sdvc.crud.services.DefaultProjectService;
import org.openmbee.sdvc.core.services.ProjectService;
import org.openmbee.sdvc.json.ElementJson;
import org.openmbee.sdvc.json.ProjectJson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.openmbee.sdvc.cameo.CameoConstants;

import java.util.Collections;
import java.util.List;


@Service("cameoProjectService")
public class CameoProjectService extends DefaultProjectService implements ProjectService {

    private NodeService nodeService;

    @Autowired
    @Qualifier("cameoNodeService")
    public void setNodeService(NodeService nodeService) {
        this.nodeService = nodeService;
    }

    @Override
    public ProjectJson create(ProjectJson project) {
        ProjectJson projectJson = super.create(project);

        ElementJson projectHoldingBin = createNode(CameoConstants.HOLDING_BIN_PREFIX + project.getProjectId(),
            "Holding Bin", projectJson);

        ElementJson viewInstanceBin = createNode(CameoConstants.VIEW_INSTANCES_BIN_PREFIX + project.getProjectId(),
            "View Instances Bin", projectJson);

        ElementsRequest elementsRequest = new ElementsRequest();
        elementsRequest.setElements(List.of(projectHoldingBin, viewInstanceBin));
        nodeService.createOrUpdate(projectJson.getProjectId(), Constants.MASTER_BRANCH, elementsRequest,
            Collections.EMPTY_MAP, projectJson.getCreator());

        return projectJson;
    }

    private static ElementJson createNode(String id, String name, ProjectJson projectJson) {
        ElementJson e = new ElementJson();
        e.setId(id);
        e.setCreator(projectJson.getCreator());
        e.setCreated(projectJson.getCreated());
        e.setModifier(projectJson.getModifier());
        e.setModified(projectJson.getModified());
        e.setName(name);
        e.put(CameoConstants.OWNERID, projectJson.getProjectId());
        e.put(CameoConstants.TYPE, CameoConstants.PACKAGE_TYPE);
        e.put(CameoConstants.ISGROUP, false);
        e.put(CameoConstants.DOCUMENTATION, "");
        e.put(CameoConstants.VISIBILITY, CameoConstants.PUBLIC_VISIBILITY);
        return e;
    }
}
