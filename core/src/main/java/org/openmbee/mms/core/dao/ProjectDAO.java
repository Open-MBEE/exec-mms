package org.openmbee.mms.core.dao;

import java.util.List;
import java.util.Optional;
import org.openmbee.mms.data.domains.global.Project;

public interface ProjectDAO {

    Optional<Project> findByProjectId(String id);

    Optional<Project> findByProjectName(String name);

    Project save(Project p);

    void delete(Project p);

    List<Project> findAll();

    List<Project> findAllByOrgId(String id);
}
