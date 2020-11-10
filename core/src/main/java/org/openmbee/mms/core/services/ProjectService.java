package org.openmbee.mms.core.services;

import org.openmbee.mms.core.objects.ProjectsResponse;
import org.openmbee.mms.json.ProjectJson;

public interface ProjectService {

    ProjectsResponse read(String projectId);

    boolean exists(String projectId);

    ProjectJson create(ProjectJson projectsPost);

    ProjectJson update(ProjectJson projectsPut);
}
