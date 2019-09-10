package org.openmbee.sdvc.rdb.repositories;

import org.openmbee.sdvc.data.domains.global.ProjectUserPerm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectUserPermRepository extends JpaRepository<ProjectUserPerm, Long> {

}
