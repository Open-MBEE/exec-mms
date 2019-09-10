package org.openmbee.sdvc.rdb.repositories;

import java.util.Optional;
import org.openmbee.sdvc.data.domains.global.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Optional<User> findByUsername(String username);

}
