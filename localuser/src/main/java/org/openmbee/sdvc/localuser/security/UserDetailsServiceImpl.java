package org.openmbee.sdvc.localuser.security;

import java.util.Optional;

import org.openmbee.sdvc.core.exceptions.ForbiddenException;
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

    @Transactional
    public void changeUserPassword(String username, String password) {
        Optional<User> userOptional = userRepository.findByUsername(username);
        if(! userOptional.isPresent()) {
            throw new UsernameNotFoundException(
                    String.format("No user found with username '%s'.", username));
        }

        User user = userOptional.get();
        if(user.getPassword() == null || user.getPassword().isEmpty()) {
            throw new ForbiddenException("Cannot change or set passwords for external users.");
        }

        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);
    }
}
