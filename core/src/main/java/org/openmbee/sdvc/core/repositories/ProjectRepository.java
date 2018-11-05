package org.openmbee.sdvc.core.repositories;

import org.openmbee.sdvc.core.domains.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectRepository extends JpaRepository<Project, String> {

    Project findByProjectId(String id);

    Project findByProjectName(String name);

}
