package org.openmbee.mms.ldap.security;

import org.openmbee.mms.core.config.AuthorizationConstants;
import org.openmbee.mms.core.exceptions.ForbiddenException;
import org.openmbee.mms.data.domains.global.User;
import org.openmbee.mms.json.GroupJson;
import org.openmbee.mms.json.UserJson;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

import org.openmbee.mms.users.objects.UsersCreateRequest;
import org.openmbee.mms.users.security.AbstractUserDetailsService;
import org.openmbee.mms.users.security.UserDetails;
import org.openmbee.mms.users.security.UserDetailsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.ldap.core.support.BaseLdapPathContextSource;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.ldap.filter.*;
import org.springframework.ldap.support.LdapEncoder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.ldap.SpringSecurityLdapTemplate;
import org.springframework.security.ldap.userdetails.LdapAuthoritiesPopulator;

@Configuration
public class LdapAuthoritiesConfig extends AbstractUserDetailsService implements UserDetailsService {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Value("${ldap.user.attributes.username:uid}")
    private String userAttributesUsername;

    @Value("${ldap.user.attributes.firstname:givenname}")
    private String userAttributesFirstName;

    @Value("${ldap.user.attributes.lastname:sn}")
    private String userAttributesLastName;

    @Value("${ldap.user.attributes.email:mail}")
    private String userAttributesEmail;

    @Value("${ldap.provider.url:#{null}}")
    private String providerUrl;

    @Value("${ldap.provider.userdn:#{null}}")
    private String providerUserDn;

    @Value("${ldap.provider.password:#{null}}")
    private String providerPassword;

    @Value("${ldap.provider.base:#{null}}")
    private String providerBase;

    @Value("#{'${ldap.user.dn.pattern:uid={0}}'.split(';')}")
    private List<String> userDnPattern;

    @Value("${ldap.user.attributes.update:24}")
    private int userAttributesUpdate;

    @Value("${ldap.group.role.attribute:cn}")
    private String groupRoleAttribute;

    @Value("${ldap.group.search.filter:(uniqueMember={0})}")
    private String groupSearchFilter;

    @Value("${ldap.group.search.base:#{''}}")
    private String groupSearchBase;


    @Bean
    LdapAuthoritiesPopulator ldapAuthoritiesPopulator(@Qualifier("contextSource") BaseLdapPathContextSource baseContextSource) {

        /*
          Specificity here : we don't get the Role by reading the members of available groups (which is implemented by
          default in Spring security LDAP), but we retrieve the groups the user belongs to.
         */
        class CustomLdapAuthoritiesPopulator implements LdapAuthoritiesPopulator {

            final SpringSecurityLdapTemplate ldapTemplate;

            private CustomLdapAuthoritiesPopulator(BaseLdapPathContextSource ldapContextSource) {
                ldapTemplate = new SpringSecurityLdapTemplate(ldapContextSource);
            }

            @Override
            public Collection<? extends GrantedAuthority> getGrantedAuthorities(
                DirContextOperations userData, String username) {
                logger.debug("Populating authorities using LDAP");
                Optional<UserJson> userOptional = getUserPersistence().findByUsername(username);

                if (userOptional.isEmpty()) {
                    logger.info("No user record for {} in the userRepository, creating...", userData.getDn());
                    UserJson newUser = register(parseLdapRegister(userData));
                    userOptional = Optional.of(newUser);
                }

                UserJson user = userOptional.get();
                if (user.getModified() != null && Instant.parse(user.getModified()).isBefore(Instant.now().minus(userAttributesUpdate, ChronoUnit.HOURS))) {
                    saveLdapUser(userData, user);
                }
                user.setPassword(null);

                StringBuilder userDnBuilder = new StringBuilder();
                userDnBuilder.append(userData.getDn().toString());
                if (providerBase != null && !providerBase.isEmpty()) {
                    userDnBuilder.append(',');
                    userDnBuilder.append(providerBase);
                }
                String userDn = userDnBuilder.toString();
                Collection<GroupJson> definedGroups = getGroupPersistence().findAll();
                OrFilter orFilter = new OrFilter();

                for (GroupJson definedGroup : definedGroups) {
                    orFilter.or(new EqualsFilter(groupRoleAttribute, definedGroup.getName()));
                }

                AndFilter andFilter = new AndFilter();
                HardcodedFilter groupsFilter = new HardcodedFilter(
                    groupSearchFilter.replace("{0}", LdapEncoder.filterEncode(userDn)));
                andFilter.and(groupsFilter);
                andFilter.and(orFilter);

                String filter = andFilter.encode();
                logger.debug("Searching LDAP with filter: {}", filter);

                Set<String> memberGroups = ldapTemplate
                    .searchForSingleAttributeValues(groupSearchBase, filter, new Object[]{""}, groupRoleAttribute);
                logger.debug("LDAP search result: {}", Arrays.toString(memberGroups.toArray()));

                getUserPersistence().save(user);
                //Add groups to user

                Set<GroupJson> addGroups = new HashSet<>();

                for (String memberGroup : memberGroups) {
                    Optional<GroupJson> group = getGroupPersistence().findByName(memberGroup);
                    group.ifPresent(g -> getUserGroupPersistence().addUserToGroup(g.getName(), user.getUsername()));
                    group.ifPresent(addGroups::add);
                }

                if (logger.isDebugEnabled()) {
                    if ((long) addGroups.size() > 0) {
                        addGroups.forEach(group -> logger.debug("Group received: {}", group.getName()));
                    } else {
                        logger.debug("No configured groups returned from LDAP");
                    }
                }

//                user.setGroups(addGroups);
//                getUserRepo().save(user);

                List<GrantedAuthority> auths = AuthorityUtils
                    .createAuthorityList(memberGroups.toArray(new String[0]));
                if (Boolean.TRUE.equals(user.isAdmin())) {
                    auths.add(new SimpleGrantedAuthority(AuthorizationConstants.MMSADMIN));
                }
                auths.add(new SimpleGrantedAuthority(AuthorizationConstants.EVERYONE));
                return auths;
            }
        }

        return new CustomLdapAuthoritiesPopulator(baseContextSource);

    }



    @Bean
    public LdapContextSource contextSource() {
        LdapContextSource contextSource = new LdapContextSource();

        logger.debug("Initializing LDAP ContextSource with the following values: ");

        contextSource.setUrl(providerUrl);
        contextSource.setBase(providerBase);
        contextSource.setUserDn(providerUserDn);
        contextSource.setPassword(providerPassword);

        logger.debug("BaseLdapPath: " + contextSource.getBaseLdapPathAsString());
        logger.debug("UserDn: " + contextSource.getUserDn());

        return contextSource;
    }


    @Override
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

    private UserJson saveLdapUser(DirContextOperations userData, UserJson saveUser) {
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

        if (saveUser.getType() == null) {
            saveUser.setType("ldap");
        }

        return saveUser;
    }

    public UsersCreateRequest parseLdapRegister(DirContextOperations userData) {
        UsersCreateRequest createUser = new UsersCreateRequest();

        createUser.setEmail(userData.getStringAttribute(userAttributesEmail));
        createUser.setFirstName(userData.getStringAttribute(userAttributesFirstName));
        createUser.setLastName(userData.getStringAttribute(userAttributesLastName));
        createUser.setUsername(userData.getStringAttribute(userAttributesUsername));
        createUser.setType("ldap");

        return createUser;
    }

    @Override
    public String encodePassword(String password) {
        throw new ForbiddenException("Cannot Modify Password. Users for this server are controlled by LDAP");
    }

    @Override
    public UserJson update(UsersCreateRequest req, UserJson user) {
        throw new ForbiddenException("Cannot Modify User. Users for this server are controlled by LDAP");
    }

    private UserJson createLdapUser(DirContextOperations userData) {
        String username = userData.getStringAttribute(userAttributesUsername);
        logger.debug("Creating user for {} using LDAP", username);
        UserJson user = saveLdapUser(userData, new UserJson());
        user.setUsername(username);
        user.setEnabled(true);
        user.setAdmin(false);
        return getUserPersistence().save(user);
    }
}