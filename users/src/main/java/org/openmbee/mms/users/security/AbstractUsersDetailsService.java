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

        if (!user.isPresent()) {
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
        Optional<Group> evGroup = getGroupRepo().findByName(AuthorizationConstants.EVERYONE);
        if (evGroup.isPresent()) {
            Group group =  evGroup.get();
            if (user.getGroups() == null) {
                user.setGroups(new HashSet<>());
            }
            if (!user.getGroups().contains(group)) {
                user.getGroups().add(group);
            }
        }
        return getUserRepo().save(user);
    }

    public List<User> getUsers() {
        return getUserRepo().findAll();
    }


}
