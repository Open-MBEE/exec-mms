package org.openmbee.sdvc.crud.services;

import org.openmbee.sdvc.json.ProjectJson;
import org.openmbee.sdvc.crud.controllers.projects.ProjectsResponse;

public interface ProjectService {

    ProjectsResponse read(String projectId);

    boolean exists(String projectId);

    ProjectJson create(ProjectJson projectsPost);

    ProjectJson update(ProjectJson projectsPut);
}
