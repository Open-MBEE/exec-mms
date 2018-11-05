package org.openmbee.sdvc.authenticator.services;

import org.openmbee.sdvc.core.domains.User;

public interface UserService {

    User save(User user);

    void delete(User user);

    User findByEmail(String email);

    User findByUsername(String username);

    Iterable<User> findAll();
}
