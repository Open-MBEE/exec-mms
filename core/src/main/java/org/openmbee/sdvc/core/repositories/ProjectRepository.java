package org.openmbee.sdvc.core.repositories;

import java.util.Optional;
import org.openmbee.sdvc.core.domains.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectRepository extends JpaRepository<Project, String> {

    Optional<Project> findByProjectId(String id);

    Optional<Project> findByProjectName(String name);

}
