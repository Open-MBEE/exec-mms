package org.openmbee.sdvc.sysml.services;

import org.openmbee.sdvc.crud.controllers.projects.ProjectsRequest;
import org.openmbee.sdvc.crud.controllers.projects.ProjectsResponse;
import org.openmbee.sdvc.crud.services.DefaultProjectService;
import org.openmbee.sdvc.crud.services.ProjectService;
import org.springframework.stereotype.Service;


@Service("cameoProjectService")
public class CameoProjectService extends DefaultProjectService implements ProjectService {

    public ProjectsResponse post(ProjectsRequest projectsPost) {
        //create elastic index with sysml specific mapping
        return super.post(projectsPost);
    }
}
