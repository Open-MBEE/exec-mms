package org.openmbee.sdvc.core.dao;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.openmbee.sdvc.json.ProjectJson;

public interface ProjectIndex {

    void create(String projectId);

    void create(String projectId, String projectType);

    Optional<ProjectJson> findById(String docId);

    List<ProjectJson> findAllById(Set<String> docIds);

    void delete(String projectId);

    void update(ProjectJson projectJson);

}
