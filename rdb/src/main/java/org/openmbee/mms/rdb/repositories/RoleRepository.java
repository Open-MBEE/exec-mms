package org.openmbee.mms.rdb.repositories;

import java.util.List;
import java.util.Optional;

import org.openmbee.mms.data.domains.global.Privilege;
import org.openmbee.mms.data.domains.global.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByName(String name);

    List<Role> findAllByPrivilegesIn(List<Privilege> privileges);

}
