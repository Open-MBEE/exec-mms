package org.openmbee.sdvc.core.services;

import org.openmbee.sdvc.core.objects.ProjectsResponse;
import org.openmbee.sdvc.json.ProjectJson;

public interface ProjectService {

    ProjectsResponse read(String projectId);

    boolean exists(String projectId);

    ProjectJson create(ProjectJson projectsPost);

    ProjectJson update(ProjectJson projectsPut);
}
