package org.openmbee.mms.twc.security;

import org.json.JSONException;
import org.json.JSONObject;
import org.openmbee.mms.core.exceptions.ForbiddenException;
import org.openmbee.mms.json.GroupJson;
import org.openmbee.mms.twc.TeamworkCloud;
import org.openmbee.mms.twc.config.TwcConfig;
import org.openmbee.mms.twc.exceptions.TwcConfigurationException;
import org.openmbee.mms.twc.utilities.AdminUtils;
import org.openmbee.mms.users.objects.UsersCreateRequest;
import org.openmbee.mms.users.security.AbstractUserDetailsService;
import org.openmbee.mms.users.security.DefaultUserDetails;
import org.openmbee.mms.users.security.UserDetails;
import org.openmbee.mms.users.security.UserDetailsService;
import org.openmbee.mms.core.dao.UserGroupsPersistence;
import org.openmbee.mms.core.dao.UserPersistence;
import org.openmbee.mms.json.UserJson;
import org.openmbee.mms.users.objects.UsersCreateRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.*;

@Service
public class TwcUserDetailsService extends AbstractUserDetailsService implements UserDetailsService {
    private AdminUtils adminUtils;
    private TwcConfig twcConfig;

    @Autowired
    public void setTwcConfig(TwcConfig twcConfig) {
        this.twcConfig = twcConfig;
    }
    public void setAdminUtils(AdminUtils adminUtils) {
        this.adminUtils = adminUtils;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        List<TeamworkCloud> twcs = twcConfig.getInstances();
        JSONObject twcUser = null;
        for (TeamworkCloud twc : twcs) {
            twcUser = adminUtils.getUserByUsername(username, twc);
            if (twcUser != null) {
                break;
            }
        }

        if (twcUser != null) {
            Optional<UserJson> userOpt = getUserPersistence().findByUsername(twcUser.getString("username"));
            if (userOpt.isEmpty()) {
                UserJson newUser = register(parseTwcRegister(twcUser));
                userOpt = Optional.of(newUser);
            }
            UserJson user = userOpt.get();
            Collection<GroupJson> groups = getUserGroupPersistence().findGroupsAssignedToUser(username);
            return new DefaultUserDetails(user, groups);
        }

        throw new UsernameNotFoundException("Username not found on any connected TWC Servers");
    }
//    @Override
    @Transactional
    public UserJson register(UsersCreateRequest req) {
        UserJson user = new UserJson();
        user.setEmail(req.getEmail());
        user.setFirstName(req.getFirstName());
        user.setLastName(req.getLastName());
        user.setUsername(req.getUsername());
        user.setEnabled(true);
        user.setAdmin(req.isAdmin());
        user.setType(req.getType());
        return saveUser(user);
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
    public UserJson update(UsersCreateRequest req, UserJson user) {
        throw new ForbiddenException("Cannot Modify User. Users for this server are controlled by Teamwork Cloud");
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
