package org.openmbee.mms.ldap.security;

import org.openmbee.mms.core.config.AuthorizationConstants;
import org.openmbee.mms.core.exceptions.ForbiddenException;
import org.openmbee.mms.data.domains.global.Group;
import org.openmbee.mms.data.domains.global.User;
import org.openmbee.mms.users.security.DefaultUsersDetailsService;
import org.openmbee.mms.users.security.UsersCreateRequest;
import org.openmbee.mms.users.security.UsersDetailsService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class LdapUsersDetailsService extends DefaultUsersDetailsService implements UsersDetailsService {
    @Value("${ldap.user.attributes.username:uid}")
    private String userAttributesUsername;

    @Value("${ldap.user.attributes.firstname:givenname}")
    private String userAttributesFirstName;

    @Value("${ldap.user.attributes.lastname:sn}")
    private String userAttributesLastName;

    @Value("${ldap.user.attributes.email:mail}")
    private String userAttributesEmail;


    @Override
    public void changeUserPassword(String username, String password, boolean asAdmin) {
        throw new ForbiddenException("Cannot Modify Password. Users for this server are controlled by LDAP");
    }


    @Override
    @Transactional
    public User register(UsersCreateRequest req) {
        User user = new User();
        user.setEmail(req.getEmail());
        user.setFirstName(req.getFirstName());
        user.setLastName(req.getLastName());
        user.setUsername(req.getUsername());
        user.setPassword(encodePassword(req.getPassword()));
        user.setEnabled(true);
        user.setAdmin(req.isAdmin());
        return saveUser(user);
    }

    @Override
    public User saveUser(User user) {

        Optional<Group> evGroup = getGroupRepo().findByName(AuthorizationConstants.EVERYONE);
        evGroup.ifPresent(group -> user.getGroups().add(group));
        return getUserRepo().save(user);
    }

    public User parseLdapUser(DirContextOperations userData, User saveUser) {
        if (saveUser.getEmail() == null ||
            !saveUser.getEmail().equals(userData.getStringAttribute(userAttributesEmail))
        ) {
            saveUser.setEmail(userData.getStringAttribute(userAttributesEmail));
        }
        if (saveUser.getFirstName() == null ||
            !saveUser.getFirstName().equals(userData.getStringAttribute(userAttributesFirstName))
        ) {
            saveUser.setFirstName(userData.getStringAttribute(userAttributesFirstName));
        }
        if (saveUser.getLastName() == null ||
            !saveUser.getLastName().equals(userData.getStringAttribute(userAttributesLastName))
        ) {
            saveUser.setLastName(userData.getStringAttribute(userAttributesLastName));
        }

        return saveUser;
    }

    public UsersCreateRequest parseLdapRegister(DirContextOperations userData) {
        UsersCreateRequest createUser = new UsersCreateRequest();
        if (createUser.getEmail() == null ||
            !createUser.getEmail().equals(userData.getStringAttribute(userAttributesEmail))
        ) {
            createUser.setEmail(userData.getStringAttribute(userAttributesEmail));
        }
        if (createUser.getFirstName() == null ||
            !createUser.getFirstName().equals(userData.getStringAttribute(userAttributesFirstName))
        ) {
            createUser.setFirstName(userData.getStringAttribute(userAttributesFirstName));
        }
        if (createUser.getLastName() == null ||
            !createUser.getLastName().equals(userData.getStringAttribute(userAttributesLastName))
        ) {
            createUser.setLastName(userData.getStringAttribute(userAttributesLastName));
        }

        return createUser;
    }







}
