package org.openmbee.mms.twc.security;

import org.openmbee.mms.data.domains.global.User;
import org.openmbee.mms.rdb.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class TwcUserDetailsService implements UserDetailsService {

    private UserRepository userRepository;

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
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
        userRepository.save(user);
        return user;
    }

}
