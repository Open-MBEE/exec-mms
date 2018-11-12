package org.openmbee.sdvc.crud.services;

import org.openmbee.sdvc.crud.controllers.projects.ProjectsRequest;
import org.openmbee.sdvc.crud.controllers.projects.ProjectsResponse;

public interface ProjectService {

    public ProjectsResponse get(String projectId);

    public ProjectsResponse post(ProjectsRequest projectsPost);
}
