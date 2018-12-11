package org.openmbee.sdvc.crud.services;

import org.openmbee.sdvc.json.ProjectJson;
import org.openmbee.sdvc.crud.controllers.projects.ProjectsResponse;

public interface ProjectService {

    ProjectsResponse get(String projectId);

    ProjectJson post(ProjectJson projectsPost);
}
