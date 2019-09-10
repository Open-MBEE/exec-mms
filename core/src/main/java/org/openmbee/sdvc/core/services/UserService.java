package org.openmbee.sdvc.core.services;

import java.util.Optional;
import org.openmbee.sdvc.data.domains.global.User;

public interface UserService {

    User save(User user);

    void delete(User user);

    Optional<User> findByEmail(String email);

    Optional<User> findByUsername(String username);

    Iterable<User> findAll();
}
