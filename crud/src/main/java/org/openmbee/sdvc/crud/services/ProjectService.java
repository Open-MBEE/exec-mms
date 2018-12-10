package org.openmbee.sdvc.crud.services;

import org.openmbee.sdvc.crud.controllers.projects.ProjectJson;
import org.openmbee.sdvc.crud.controllers.projects.ProjectsRequest;
import org.openmbee.sdvc.crud.controllers.projects.ProjectsResponse;

public interface ProjectService {

    ProjectsResponse get(String projectId);

    ProjectJson post(ProjectJson projectsPost);
}
