package org.openmbee.mms.users.security;

import org.openmbee.mms.core.config.AuthorizationConstants;
import org.openmbee.mms.core.exceptions.ForbiddenException;
import org.openmbee.mms.data.domains.global.Group;
import org.openmbee.mms.data.domains.global.User;
import org.openmbee.mms.rdb.repositories.GroupRepository;
import org.openmbee.mms.rdb.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class DefaultUsersDetailsService implements UsersDetailsService {

    private UserRepository userRepository;
    private GroupRepository groupRepository;

    public GroupRepository getGroupRepo() {
        return this.groupRepository;
    }

    public UserRepository getUserRepo() {
        return this.userRepository;
    }

    protected PasswordEncoder passwordEncoder;

    @Autowired
    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
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
        evGroup.ifPresent(group -> user.getGroups().add(group));
        return getUserRepo().save(user);
    }

    public List<User> getUsers() {
        return getUserRepo().findAll();
    }


    @Transactional
    public void changeUserPassword(String username, String password, boolean asAdmin) {
        Optional<User> userOptional = getUserRepo().findByUsername(username);
        if(! userOptional.isPresent()) {
            throw new UsernameNotFoundException(
                String.format("No user found with username '%s'.", username));
        }

        User user = userOptional.get();
        if(!asAdmin && (user.getPassword() == null || user.getPassword().isEmpty())) {
            throw new ForbiddenException("Cannot change or set passwords for external users.");
        }

        //TODO password strength test?
        user.setPassword(encodePassword(password));
        getUserRepo().save(user);
    }

    protected String encodePassword(String password) {
        return (password != null && !password.isEmpty()) ? passwordEncoder.encode(password) : null;
    }


}
