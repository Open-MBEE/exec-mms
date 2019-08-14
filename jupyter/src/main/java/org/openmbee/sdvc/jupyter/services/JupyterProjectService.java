package org.openmbee.sdvc.jupyter.services;

import org.openmbee.sdvc.crud.controllers.projects.ProjectsResponse;
import org.openmbee.sdvc.crud.services.DefaultProjectService;
import org.openmbee.sdvc.core.services.ProjectService;
import org.springframework.stereotype.Service;


@Service("jupyterProjectService")
public class JupyterProjectService extends DefaultProjectService implements ProjectService<ProjectsResponse> {

}
