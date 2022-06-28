package org.openmbee.mms.users.security;

import org.openmbee.mms.data.domains.global.User;
import org.openmbee.mms.users.objects.UsersCreateRequest;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;

public interface UsersDetailsService extends org.springframework.security.core.userdetails.UserDetailsService {

    @Override
    UsersDetails loadUserByUsername(String username) throws UsernameNotFoundException;

    User register(UsersCreateRequest req);

    User saveUser(User user);

    void changeUserPassword(String username, String password, boolean asAdmin);

    String encodePassword(String password);


    List<User> getUsers();

    User update(UsersCreateRequest req, User user);
}
