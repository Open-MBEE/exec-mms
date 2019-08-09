package org.openmbee.sdvc.core.services;

import org.openmbee.sdvc.json.ProjectJson;

public interface ProjectService<T> {

    T read(String projectId);

    boolean exists(String projectId);

    ProjectJson create(ProjectJson projectsPost);

    ProjectJson update(ProjectJson projectsPut);
}
