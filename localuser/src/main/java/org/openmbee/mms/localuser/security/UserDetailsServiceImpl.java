package org.openmbee.mms.localuser.security;

import java.util.List;
import java.util.Optional;

import org.openmbee.mms.core.exceptions.ForbiddenException;
import org.openmbee.mms.rdb.repositories.UserRepository;
import org.openmbee.mms.data.domains.global.User;
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

    public List<User> getUsers() {
        return userRepository.findAll();
    }

    @Transactional
    public User register(UserCreateRequest req) {
        User user = new User();
        user.setEmail(req.getEmail());
        user.setFirstName(req.getFirstname());
        user.setLastName(req.getLastname());
        user.setUsername(req.getUsername());
        user.setPassword(encodePassword(req.getPassword()));
        user.setEnabled(true);
        user.setAdmin(req.isAdmin());
        return userRepository.save(user);
    }

    @Transactional
    public void changeUserPassword(String username, String password, boolean asAdmin) {
        Optional<User> userOptional = userRepository.findByUsername(username);
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
        userRepository.save(user);
    }

    private String encodePassword(String password) {
        return (password != null && !password.isEmpty()) ? passwordEncoder.encode(password) : null;
    }
}
