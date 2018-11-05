package org.openmbee.sdvc.core.repositories;

import org.openmbee.sdvc.core.domains.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, String> {

    Role findByName(String name);

}
