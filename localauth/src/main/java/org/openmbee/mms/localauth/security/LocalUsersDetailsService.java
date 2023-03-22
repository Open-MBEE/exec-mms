package org.openmbee.mms.localauth.security;

import java.util.List;
import java.util.Optional;

import org.openmbee.mms.core.exceptions.ForbiddenException;
import org.openmbee.mms.data.domains.global.User;
import org.openmbee.mms.users.security.AbstractUsersDetailsService;
import org.openmbee.mms.users.objects.UsersCreateRequest;
import org.openmbee.mms.users.security.UsersDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class LocalUsersDetailsService extends AbstractUsersDetailsService implements UsersDetailsService {


    protected PasswordEncoder passwordEncoder;

    @Autowired
    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void changeUserPassword(String username, String password, boolean asAdmin) {
        Optional<User> userOptional = getUserRepo().findByUsername(username);
        if(! userOptional.isPresent()) {
            throw new UsernameNotFoundException(
                String.format("No user found with username '%s'.", username));
        }

        User user = userOptional.get();
        if (user.getType() == User.VALID_USER_TYPES.REMOTE || user.getPassword() == null) {
            throw new ForbiddenException("Cannot change or set passwords for external users.");
        }

        //TODO password strength test?
        user.setPassword(encodePassword(password));
        getUserRepo().save(user);
    }

    @Transactional
    public User register(UsersCreateRequest req) {
        User user = new User();
        user.setEmail(req.getEmail());
        user.setFirstName(req.getFirstName());
        user.setLastName(req.getLastName());
        user.setUsername(req.getUsername());
        if (req.getType() == null) {
            req.setType("local");
        }
        if (req.getType().equals("local") && !(req.getPassword() == null)) {
            user.setPassword(encodePassword(req.getPassword()));
        }
        user.setEnabled(true);
        user.setAdmin(req.isAdmin());
        user.setType(req.getType());
        return saveUser(user);
    }

    public String encodePassword(String password) {
        return (password != null && !password.isEmpty()) ? passwordEncoder.encode(password) : null;
    }
}
