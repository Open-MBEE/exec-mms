package org.openmbee.sdvc.authenticator.services;

import java.util.Optional;
import org.openmbee.sdvc.authenticator.security.UserDetailsImpl;
import org.openmbee.sdvc.rdb.repositories.UserRepository;
import org.openmbee.sdvc.data.domains.global.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Autowired
    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetailsImpl loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findByUsername(username);

        if (!user.isPresent()) {
            throw new UsernameNotFoundException(
                String.format("No user found with username '%s'.", username));
        }
        return new UserDetailsImpl(user.get());
    }

    @Transactional
    public User register(String username, String password, boolean isAdmin) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setEnabled(true);
        user.setAdmin(isAdmin);
        return userRepository.save(user);
    }

}
