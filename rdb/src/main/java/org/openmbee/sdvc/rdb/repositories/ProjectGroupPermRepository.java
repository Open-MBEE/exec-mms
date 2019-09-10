package org.openmbee.sdvc.rdb.repositories;

import org.openmbee.sdvc.data.domains.global.ProjectGroupPerm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectGroupPermRepository extends JpaRepository<ProjectGroupPerm, Long> {

}
