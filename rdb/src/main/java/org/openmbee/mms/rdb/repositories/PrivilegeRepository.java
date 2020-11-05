package org.openmbee.mms.rdb.repositories;

import java.util.Optional;
import org.openmbee.mms.data.domains.global.Privilege;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PrivilegeRepository extends JpaRepository<Privilege, Long> {

    Optional<Privilege> findByName(String name);
}
