package org.openmbee.sdvc.cameo.services;

import org.openmbee.sdvc.json.ProjectJson;
import org.openmbee.sdvc.crud.services.DefaultProjectService;
import org.openmbee.sdvc.crud.services.ProjectService;
import org.springframework.stereotype.Service;


@Service("cameoProjectService")
public class CameoProjectService extends DefaultProjectService implements ProjectService {

    public ProjectJson create(ProjectJson projectsPost) {
        //TODO create elastic index with cameo specific mapping
        return super.create(projectsPost);
    }
}
