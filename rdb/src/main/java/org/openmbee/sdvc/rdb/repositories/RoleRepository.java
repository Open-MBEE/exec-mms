package org.openmbee.sdvc.rdb.repositories;

import java.util.Optional;
import org.openmbee.sdvc.data.domains.global.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByName(String name);

}
