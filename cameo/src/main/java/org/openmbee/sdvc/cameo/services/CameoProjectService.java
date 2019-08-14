package org.openmbee.sdvc.cameo.services;

import org.openmbee.sdvc.crud.controllers.projects.ProjectsResponse;
import org.openmbee.sdvc.crud.services.DefaultProjectService;
import org.openmbee.sdvc.core.services.ProjectService;
import org.springframework.stereotype.Service;


@Service("cameoProjectService")
public class CameoProjectService extends DefaultProjectService implements ProjectService<ProjectsResponse> {

}
