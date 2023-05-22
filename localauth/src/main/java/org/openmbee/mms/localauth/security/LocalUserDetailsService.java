package org.openmbee.mms.localauth.security;

import java.util.Collection;
import java.util.Optional;

import org.openmbee.mms.core.exceptions.ForbiddenException;
import org.openmbee.mms.users.objects.UsersCreateRequest;
import org.openmbee.mms.users.security.*;
import org.openmbee.mms.json.UserJson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
public class LocalUserDetailsService extends AbstractUserDetailsService implements UserDetailsService {

    protected UserPasswordRulesConfig userPasswordRulesConfig;
    protected PasswordEncoder passwordEncoder;

    @Autowired
    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Autowired
    public void setUserPasswordRulesConfig(UserPasswordRulesConfig userPasswordRulesConfig) {
        this.userPasswordRulesConfig = userPasswordRulesConfig;
    }

    public void changeUserPassword(String username, String password, boolean asAdmin) {
        Optional<UserJson> userOptional = getUserPersistence().findByUsername(username);
        if(userOptional.isEmpty()) {
            throw new UsernameNotFoundException(
                String.format("No user found with username '%s'.", username));
        }

        UserJson user = userOptional.get();
        if(!asAdmin && !userPasswordRulesConfig.isAllowSelfSetPasswordsWhenBlank() &&
            (!user.getType().equals("local")  || user.getPassword() == null || user.getPassword().isBlank())) {
            throw new ForbiddenException("Cannot change or set passwords for external users.");
        }

        //TODO password strength test?
        user.setPassword(encodePassword(password));
        getUserPersistence().save(user);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<UserJson> user = getUserPersistence().findByUsername(username);

        if (user.isEmpty()) {
            throw new UsernameNotFoundException(
                String.format("No user found with username '%s'.", username));
        }
        return new DefaultUserDetails(user.get(), getUserGroupPersistence().findGroupsAssignedToUser(username));
    }

    public Collection<UserJson> getUsers() {
        return getUserPersistence().findAll();
    }

    public UserJson register(UsersCreateRequest req) {
        UserJson user = new UserJson();
        user.setUsername(req.getUsername());
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
        return getUserPersistence().save(user);
    }



    public String encodePassword(String password) {
        return (password != null && !password.isBlank()) ? passwordEncoder.encode(password) : null;
    }
}
