package org.openmbee.mms.users.security;

import org.openmbee.mms.json.UserJson;
import org.openmbee.mms.users.objects.UsersCreateRequest;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Collection;
import java.util.List;

public interface UserDetailsService extends org.springframework.security.core.userdetails.UserDetailsService {

    @Override
    UserDetails loadUserByUsername(String username) throws UsernameNotFoundException;

    UserJson register(UsersCreateRequest req);

    UserJson saveUser(UserJson user);

    void changeUserPassword(String username, String password, boolean asAdmin);

    String encodePassword(String password);


    Collection<UserJson> getUsers();

    UserJson update(UsersCreateRequest req, UserJson user);
}
