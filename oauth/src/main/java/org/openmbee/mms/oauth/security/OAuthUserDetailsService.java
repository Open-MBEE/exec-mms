package org.openmbee.mms.oauth.security;

import org.openmbee.mms.core.exceptions.ForbiddenException;
import org.openmbee.mms.json.UserJson;
import org.openmbee.mms.users.objects.UsersCreateRequest;
import org.openmbee.mms.users.security.AbstractUserDetailsService;
import org.openmbee.mms.users.security.UserDetails;
import org.openmbee.mms.users.security.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class OAuthUserDetailsService extends AbstractUserDetailsService implements UserDetailsService {

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<UserJson> userOptional = getUserPersistence().findByUsername(username);

        UserJson user;
        if (userOptional.isEmpty()) {
            user = addUser(username);
        } else {
            user = userOptional.get();
        }
        return new OAuthUserDetails(user, getUserGroupPersistence().findGroupsAssignedToUser(username));
    }

    public UserJson addUser(String username) {
        UserJson user = new UserJson();
        user.setUsername(username);
        //TODO: fill in user details from TWC
        user.setEnabled(true);
        return getUserPersistence().save(user);
    }

    @Override
    public String encodePassword(String password) {
        throw new ForbiddenException("Cannot Modify Password. Users for this server are controlled via OAuth");
    }

    @Override
    public UserJson update(UsersCreateRequest req, UserJson user) {
        throw new ForbiddenException("Cannot Modify User. Users for this server are controlled via OAuth");
    }

}
