package org.openmbee.mms.localauth.security;

import java.util.List;

import org.openmbee.mms.data.domains.global.User;
import org.openmbee.mms.users.security.DefaultUsersDetailsService;
import org.openmbee.mms.users.security.UsersCreateRequest;
import org.openmbee.mms.users.security.UsersDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LocalUsersDetailsService extends DefaultUsersDetailsService implements UsersDetailsService {




    public List<User> getUsers() {
        return getUserRepo().findAll();
    }

    @Transactional
    public User register(UsersCreateRequest req) {
        User user = new User();
        user.setEmail(req.getEmail());
        user.setFirstName(req.getFirstName());
        user.setLastName(req.getLastName());
        user.setUsername(req.getUsername());
        user.setPassword(encodePassword(req.getPassword()));
        user.setEnabled(true);
        user.setAdmin(req.isAdmin());
        return saveUser(user);
    }


}
