package org.openmbee.sdvc.rdb.repositories;

import java.util.Optional;
import org.openmbee.sdvc.data.domains.global.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {

    Optional<Project> findByProjectId(String id);

    Optional<Project> findByProjectName(String name);

}
