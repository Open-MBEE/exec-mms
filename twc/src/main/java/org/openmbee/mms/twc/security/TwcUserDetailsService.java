package org.openmbee.mms.twc.security;

import org.openmbee.mms.data.domains.global.User;
import org.openmbee.mms.twc.TeamworkCloud;
import org.openmbee.mms.twc.config.TwcConfig;
import org.openmbee.mms.twc.utilities.AdminUtils;
import org.openmbee.mms.users.security.UsersCreateRequest;
import org.openmbee.mms.users.security.UsersDetails;
import org.openmbee.mms.users.security.UsersDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class TwcUserDetailsService implements UsersDetailsService {

    private AdminUtils adminUtils;
    private TwcConfig twcConfig;

    @Autowired
    public void setTwcConfig(TwcConfig twcConfig) {
        this.twcConfig = twcConfig;
    }

    @Autowired
    public void setAdminUtils(AdminUtils adminUtils) {
        this.adminUtils = adminUtils;
    }

    @Override
    public UsersDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        List<TeamworkCloud> twcs = twcConfig.getInstances();
        Optional<User> user = Optional.empty();
        for (TeamworkCloud twc: twcs) {
            user = adminUtils.getUserByUsername(username, twc);
            if (user.isPresent()) {
                break;
            }
        }


        User u;
        u = user.orElseGet(() -> addUser(username));
        return new TwcUserDetails(u);
    }

    @Override
    public User register(UsersCreateRequest req) {
        return null;
    }

    @Override
    public User saveUser(User user) {
        return null;
    }

    @Override
    public void changeUserPassword(String username, String password, boolean asAdmin) {

    }

    @Override
    public List<User> getUsers() {
        return null;
    }

    @Transactional
    public User addUser(String username) {
        User user = new User();
        user.setUsername(username);
        //TODO: fill in user details from TWC
        user.setEnabled(true);
        return saveUser(user);
    }

}
