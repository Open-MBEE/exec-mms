package org.openmbee.sdvc.rdb.repositories;

import org.openmbee.sdvc.data.domains.global.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupRepository extends JpaRepository<Group, Long> {

}
