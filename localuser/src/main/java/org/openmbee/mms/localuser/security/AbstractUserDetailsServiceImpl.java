package org.openmbee.mms.localuser.security;

import org.openmbee.mms.core.config.AuthorizationConstants;
import org.openmbee.mms.data.domains.global.Group;
import org.openmbee.mms.data.domains.global.User;
import org.openmbee.mms.rdb.repositories.GroupRepository;
import org.openmbee.mms.rdb.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Optional;

public abstract class AbstractUserDetailsServiceImpl implements UserDetailsService {

    private UserRepository userRepository;
    private GroupRepository groupRepository;

    public GroupRepository getGroupRepo() {
        return this.groupRepository;
    }

    public UserRepository getUserRepo() {
        return this.userRepository;
    }

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Autowired
    public void setGroupRepository(GroupRepository groupRepository) {
        this.groupRepository = groupRepository;
    }

    public User saveUser(User user) {
        Optional<Group> evGroup = getGroupRepo().findByName(AuthorizationConstants.EVERYONE);
        evGroup.ifPresent(group -> user.getGroups().add(group));
        return getUserRepo().save(user);
    }
}
