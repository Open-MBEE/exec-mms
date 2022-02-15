package org.openmbee.mms.twc.security;

import org.openmbee.mms.core.config.AuthorizationConstants;
import org.openmbee.mms.data.domains.global.Group;
import org.openmbee.mms.data.domains.global.User;
import org.openmbee.mms.rdb.repositories.GroupRepository;
import org.openmbee.mms.rdb.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class TwcUserDetailsService implements UserDetailsService {

    private UserRepository userRepository;
    private GroupRepository groupRepository;

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Autowired
    public void setGroupRepository(GroupRepository groupRepository) {
        this.groupRepository = groupRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findByUsername(username);

        User u;
        if (!user.isPresent()) {
            u = addUser(username);
        } else {
            u = user.get();
        }
        return new TwcUserDetails(u);
    }

    @Transactional
    public User addUser(String username) {
        User user = new User();
        user.setUsername(username);
        //TODO: fill in user details from TWC
        user.setEnabled(true);
        Optional<Group> evGroup = groupRepository.findByName(AuthorizationConstants.EVERYONE);
        if (evGroup.isPresent()) {
            evGroup.get().getUsers().add(user);
            groupRepository.save(evGroup.get());
        }
        userRepository.save(user);
        return user;
    }

}
