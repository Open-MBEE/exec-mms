package org.openmbee.mms.twc.security;

import org.json.JSONException;
import org.json.JSONObject;
import org.openmbee.mms.data.domains.global.User;
import org.openmbee.mms.twc.TeamworkCloud;
import org.openmbee.mms.twc.config.TwcConfig;
import org.openmbee.mms.twc.exceptions.TwcConfigurationException;
import org.openmbee.mms.twc.utilities.AdminUtils;
import org.openmbee.mms.users.objects.UsersCreateRequest;
import org.openmbee.mms.users.security.AbstractUsersDetailsService;
import org.openmbee.mms.users.security.DefaultUsersDetails;
import org.openmbee.mms.users.security.UsersDetails;
import org.openmbee.mms.users.security.UsersDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.*;

@Service
public class TwcUserDetailsService extends AbstractUsersDetailsService implements UsersDetailsService {

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
        JSONObject twcUser = null;
        for (TeamworkCloud twc: twcs) {
            twcUser = adminUtils.getUserByUsername(username, twc);
            if (twcUser != null) {
                break;
            }
        }

        if (twcUser != null) {
            Optional<User> user = getUserRepo().findByUsername(twcUser.getString("username"));
            if (user.isEmpty()) {
                User newUser = register(parseTwcRegister(twcUser));
                user = Optional.of(newUser);
            }
            User u = user.get();
            return new DefaultUsersDetails(u);
        }

        throw new UsernameNotFoundException("Username not found on any connected TWC Servers");
    }

    @Override
    @Transactional
    public User register(UsersCreateRequest req) {
        User user = new User();
        user.setEmail(req.getEmail());
        user.setFirstName(req.getFirstName());
        user.setLastName(req.getLastName());
        user.setUsername(req.getUsername());
        user.setEnabled(true);
        user.setAdmin(req.isAdmin());
        user.setType(req.getType());
        return saveUser(user);
    }

    @Override
    public User saveUser(User user) {
        return null;
    }

    public void changeUserPassword(String username, String password, boolean asAdmin) {
        throw new TwcConfigurationException(HttpStatus.BAD_REQUEST,
            "Cannot Modify Password. Users for this server are controlled by Teamwork Cloud");
    }

    public String encodePassword(String password) {
        throw new TwcConfigurationException(HttpStatus.BAD_REQUEST,
                "Cannot Modify Password. Users for this server are controlled by Teamwork Cloud");
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
        user.setType("twc");
        return saveUser(user);
    }

    public UsersCreateRequest parseTwcRegister(JSONObject userData) {
        UsersCreateRequest createUser = new UsersCreateRequest();

        createUser.setUsername(userData.getString("username"));
        JSONObject otherAttributes = userData.getJSONObject("otherAttributes");
        if (otherAttributes.has("mail")) {
            createUser.setEmail(otherAttributes.getString("mail"));
        }
        if (otherAttributes.has("name")) {
            Map<String, String> name = getFirstAndLastName(otherAttributes.getString("name"));
            createUser.setFirstName(name.get("firstName"));
            createUser.setLastName(name.get("lastName"));
        }
        createUser.setType("twc");

        return createUser;
    }

    private static Map<String, String> getFirstAndLastName(String fullName) {

        Map<String, String> firstAndLastName = new HashMap<>();

        if (StringUtils.hasLength(fullName)) {
            String[] nameParts = fullName.trim().split(" ");

            /*
             * Remove Name Suffixes.
             */
            if (nameParts.length > 2 && nameParts[nameParts.length - 1].length() <= 3) {
                nameParts = Arrays.copyOf(nameParts, nameParts.length - 1);
            }

            if (nameParts.length == 2) {
                firstAndLastName.put("firstName", nameParts[0]);
                firstAndLastName.put("lastName", nameParts[1]);
            }

            if (nameParts.length > 2) {
                firstAndLastName.put("firstName", nameParts[0]);
                firstAndLastName.put("lastName", nameParts[nameParts.length - 1]);
            }
        }

        return firstAndLastName;

    }


}
