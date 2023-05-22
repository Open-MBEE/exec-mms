package org.openmbee.mms.users.security;

import org.openmbee.mms.core.dao.GroupPersistence;
import org.openmbee.mms.core.dao.UserGroupsPersistence;
import org.openmbee.mms.core.dao.UserPersistence;
import org.openmbee.mms.core.exceptions.ForbiddenException;
import org.openmbee.mms.json.GroupJson;
import org.openmbee.mms.json.UserJson;
import org.openmbee.mms.users.objects.UsersCreateRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public abstract class AbstractUserDetailsService implements UserDetailsService {

    private UserPersistence userPersistence;
    private UserGroupsPersistence userGroupsPersistence;
    private GroupPersistence groupPersistence;

    @Autowired
    public void setUserPersistence(UserPersistence userPersistence) {
        this.userPersistence = userPersistence;
    }

    @Autowired
    public void setUserGroupsPersistence(UserGroupsPersistence userGroupsPersistence) {
        this.userGroupsPersistence = userGroupsPersistence;
    }

    @Autowired
    public void setGroupPersistence(GroupPersistence groupPersistence) {
        this.groupPersistence = groupPersistence;
    }

    public UserPersistence getUserPersistence() {
        return this.userPersistence;
    }

    public GroupPersistence getGroupPersistence() {
        return this.groupPersistence;
    }

    public UserGroupsPersistence getUserGroupPersistence() {
        return this.userGroupsPersistence;
    }



    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserJson user = getUserPersistence().findByUsername(username).orElseThrow(() -> new UsernameNotFoundException(
            String.format("No user found with username '%s'.", username)));

        if (user.isEmpty()) {
            throw new UsernameNotFoundException(
                String.format("No user found with username '%s'.", username));
        }
        Collection<GroupJson> groups = getUserGroupPersistence().findGroupsAssignedToUser(username);
        return new DefaultUserDetails(user, groups);
    }


    public UserJson register(UsersCreateRequest req) {
        UserJson user = new UserJson();
        user.setUsername(req.getUsername());
        user.setEnabled(true);
        return saveUser(user);
    }

    public UserJson saveUser(UserJson user) {
        return getUserPersistence().save(user);
    }


    public void changeUserPassword(String username, String password, boolean asAdmin) {
        throw new ForbiddenException("Cannot change or set passwords for external users.");
    }


    public String encodePassword(String password) {
        throw new ForbiddenException("Cannot Modify Password. Users for this server are controlled by a Remote Service");
    }

    public UserJson update(UsersCreateRequest req, UserJson user) {
        if (req.getEmail() != null &&
            !user.getEmail().equals(req.getEmail())
        ) {
            user.setEmail(req.getEmail());
        }
        if (req.getFirstName() != null &&
            !user.getFirstName().equals(req.getFirstName())
        ) {
            user.setFirstName(req.getFirstName());
        }
        if (req.getLastName() != null &&
            !user.getLastName().equals(req.getLastName())
        ) {
            user.setLastName(req.getLastName());
        }
        if (req.isEnabled() != null && user.isEnabled() != req.isEnabled())

        if (req.getType() != null) {
            user.setType(req.getType());
        }
        return user;
    }

    public Collection<UserJson> getUsers() {
        return getUserPersistence().findAll();
    }


}
