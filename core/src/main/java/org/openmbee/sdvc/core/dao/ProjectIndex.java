package org.openmbee.sdvc.core.dao;

import org.openmbee.sdvc.json.ProjectJson;

public interface ProjectIndex {

    void create(String projectId);

    void create(String projectId, String projectType);

    void delete(String projectId);

    void update(ProjectJson projectJson);

}
