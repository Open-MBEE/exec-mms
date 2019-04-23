package org.openmbee.sdvc.core.repositories;

import java.util.Optional;
import org.openmbee.sdvc.data.domains.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, String> {

    Optional<Role> findByName(String name);

}
