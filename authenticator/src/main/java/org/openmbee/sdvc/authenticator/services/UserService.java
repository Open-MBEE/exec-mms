package org.openmbee.sdvc.authenticator.services;

import java.util.Optional;
import org.openmbee.sdvc.core.domains.User;

public interface UserService {

    User save(User user);

    void delete(User user);

    Optional<User> findByEmail(String email);

    Optional<User> findByUsername(String username);

    Iterable<User> findAll();
}
