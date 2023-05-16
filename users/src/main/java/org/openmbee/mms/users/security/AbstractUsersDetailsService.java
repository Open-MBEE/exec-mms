package org.openmbee.mms.users.security;

import org.openmbee.mms.core.config.AuthorizationConstants;
import org.openmbee.mms.data.domains.global.Group;
import org.openmbee.mms.data.domains.global.User;
import org.openmbee.mms.rdb.repositories.GroupRepository;
import org.openmbee.mms.rdb.repositories.UserRepository;
import org.openmbee.mms.users.objects.UsersCreateRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@Service
public abstract class AbstractUsersDetailsService implements UsersDetailsService {

    private UserRepository userRepository;
    private GroupRepository groupRepository;

    public GroupRepository getGroupRepo() {
        return this.groupRepository;
    }

    public UserRepository getUserRepo() {
        return this.userRepository;
    }

    @Override
    public UsersDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = getUserRepo().findByUsername(username);

        if (user.isEmpty()) {
            throw new UsernameNotFoundException(
                String.format("No user found with username '%s'.", username));
        }
        return new DefaultUsersDetails(user.get());
    }

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Autowired
    public void setGroupRepository(GroupRepository groupRepository) {
        this.groupRepository = groupRepository;
    }

    @Override
    public User register(UsersCreateRequest req) {
        User user = new User();
        user.setUsername(req.getUsername());
        user.setEnabled(true);
        return saveUser(user);
    }

    public User saveUser(User user) {
        return getUserRepo().save(user);
    }

    public User update(UsersCreateRequest req, User user) {
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

    public List<User> getUsers() {
        return getUserRepo().findAll();
    }


}
