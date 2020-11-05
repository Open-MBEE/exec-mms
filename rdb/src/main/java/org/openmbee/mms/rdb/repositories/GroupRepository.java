package org.openmbee.mms.rdb.repositories;

import java.util.Optional;
import org.openmbee.mms.data.domains.global.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupRepository extends JpaRepository<Group, Long> {

    Optional<Group> findByName(String name);
}
