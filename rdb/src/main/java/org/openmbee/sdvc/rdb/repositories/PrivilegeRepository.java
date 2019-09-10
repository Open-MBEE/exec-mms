package org.openmbee.sdvc.rdb.repositories;

import org.openmbee.sdvc.data.domains.global.Privilege;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PrivilegeRepository extends JpaRepository<Privilege, Long> {

}
