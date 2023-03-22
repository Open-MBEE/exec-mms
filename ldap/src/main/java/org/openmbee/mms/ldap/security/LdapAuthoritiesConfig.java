package org.openmbee.mms.ldap.security;

import org.openmbee.mms.core.config.AuthorizationConstants;
<<<<<<< HEAD:ldap/src/main/java/org/openmbee/mms/ldap/security/LdapAuthoritiesConfig.java
import org.openmbee.mms.core.exceptions.ForbiddenException;
=======
>>>>>>> 376ac8b4b6003b4d39619d17561dcc10ab42a61c:ldap/src/main/java/org/openmbee/mms/ldap/LdapSecurityConfig.java
import org.openmbee.mms.data.domains.global.Group;
import org.openmbee.mms.data.domains.global.User;
import org.openmbee.mms.users.objects.UsersCreateRequest;
import org.openmbee.mms.users.security.AbstractUsersDetailsService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.ldap.core.support.BaseLdapPathContextSource;
<<<<<<< HEAD:ldap/src/main/java/org/openmbee/mms/ldap/security/LdapAuthoritiesConfig.java
import org.springframework.ldap.filter.AndFilter;
import org.springframework.ldap.filter.EqualsFilter;
import org.springframework.ldap.filter.HardcodedFilter;
import org.springframework.ldap.filter.OrFilter;
=======
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.ldap.filter.*;
import org.springframework.ldap.support.LdapEncoder;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configurers.ldap.LdapAuthenticationProviderConfigurer;
>>>>>>> 376ac8b4b6003b4d39619d17561dcc10ab42a61c:ldap/src/main/java/org/openmbee/mms/ldap/LdapSecurityConfig.java
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.ldap.SpringSecurityLdapTemplate;
import org.springframework.security.ldap.authentication.ad.ActiveDirectoryLdapAuthenticationProvider;
import org.springframework.security.ldap.userdetails.LdapAuthoritiesPopulator;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

import javax.naming.Context;

@Configuration
public class LdapAuthoritiesConfig extends AbstractUsersDetailsService {
    @Value("${ldap.user.attributes.username:uid}")
    private String userAttributesUsername;

    @Value("${ldap.user.attributes.firstname:givenname}")
    private String userAttributesFirstName;

    @Value("${ldap.user.attributes.lastname:sn}")
    private String userAttributesLastName;

    @Value("${ldap.user.attributes.email:mail}")
    private String userAttributesEmail;

    @Value("${ldap.ad.enabled:false}")
    private Boolean adEnabled;

    @Value("${ldap.ad.domain:#{null}}")
    private String adDomain;

    @Value("${ldap.provider.url:#{null}}")
    private String providerUrl;

    @Value("${ldap.provider.userdn:#{null}}")
    private String providerUserDn;

    @Value("${ldap.provider.password:#{null}}")
    private String providerPassword;

    @Value("${ldap.provider.base:#{null}}")
    private String providerBase;

<<<<<<< HEAD:ldap/src/main/java/org/openmbee/mms/ldap/security/LdapAuthoritiesConfig.java
=======
    @Value("#{'${ldap.user.dn.pattern:uid={0}}'.split(';')}")
    private List<String> userDnPattern;

    @Value("${ldap.user.attributes.username:uid}")
    private String userAttributesUsername;

    @Value("${ldap.user.attributes.firstname:givenname}")
    private String userAttributesFirstName;

    @Value("${ldap.user.attributes.lastname:sn}")
    private String userAttributesLastName;

    @Value("${ldap.user.attributes.email:mail}")
    private String userAttributesEmail;
>>>>>>> 376ac8b4b6003b4d39619d17561dcc10ab42a61c:ldap/src/main/java/org/openmbee/mms/ldap/LdapSecurityConfig.java

    @Value("${ldap.user.attributes.update:24}")
    private int userAttributesUpdate;

    @Value("${ldap.group.role.attribute:cn}")
    private String groupRoleAttribute;

    @Value("${ldap.group.search.filter:(uniqueMember={0})}")
    private String groupSearchFilter;

<<<<<<< HEAD:ldap/src/main/java/org/openmbee/mms/ldap/security/LdapAuthoritiesConfig.java
=======
    @Value("${ldap.user.search.base:#{''}}")
    private String userSearchBase;

    @Value("${ldap.user.search.filter:(uid={0})}")
    private String userSearchFilter;

    private UserRepository userRepository;
    private GroupRepository groupRepository;

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Autowired
    public void setGroupRepository(GroupRepository groupRepository) {
        this.groupRepository = groupRepository;
    }

    @Autowired
    public void configureLdapAuth(AuthenticationManagerBuilder auth,
        LdapAuthoritiesPopulator ldapAuthoritiesPopulator, @Qualifier("contextSource") BaseLdapPathContextSource contextSource)
        throws Exception {
        if (providerUrl != null) {
            logger.info("LDAP Module is loading...");
        /*
            see this article : https://spring.io/guides/gs/authenticating-ldap/
            We  redefine our own LdapAuthoritiesPopulator which need ContextSource().
            We need to delegate the creation of the contextSource out of the builder-configuration.
        */
            if (adEnabled) {
                auth.authenticationProvider(activeDirectoryLdapAuthenticationProvider());
            } else {
                String[] userPatterns = userDnPattern.toArray(new String[0]);
                LdapAuthenticationProviderConfigurer<AuthenticationManagerBuilder> authProviderConfigurer = auth.ldapAuthentication();
                authProviderConfigurer.userDnPatterns(userPatterns);
                authProviderConfigurer.userSearchBase(userSearchBase);
                authProviderConfigurer.userSearchFilter(userSearchFilter);
                authProviderConfigurer.groupSearchBase(groupSearchBase);
                authProviderConfigurer.groupRoleAttribute(groupRoleAttribute);
                authProviderConfigurer.groupSearchFilter(groupSearchFilter);
                authProviderConfigurer.rolePrefix("");
                authProviderConfigurer.ldapAuthoritiesPopulator(ldapAuthoritiesPopulator);
                authProviderConfigurer.contextSource(contextSource);
            }
        }
    }

>>>>>>> 376ac8b4b6003b4d39619d17561dcc10ab42a61c:ldap/src/main/java/org/openmbee/mms/ldap/LdapSecurityConfig.java
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
<<<<<<< HEAD:ldap/src/main/java/org/openmbee/mms/ldap/security/LdapAuthoritiesConfig.java
                Optional<User> userOptional = getUserRepo().findByUsername(username);

                if (userOptional.isEmpty()) {
                    User newUser = register(parseLdapRegister(userData));

=======
                logger.debug("Populating authorities using LDAP");
                Optional<User> userOptional = userRepository.findByUsernameIgnoreCase(username);

                if (userOptional.isEmpty()) {
                    logger.info("No user record for {} in the userRepository, creating...", userData.getDn());
                    User newUser = createLdapUser(userData);
>>>>>>> 376ac8b4b6003b4d39619d17561dcc10ab42a61c:ldap/src/main/java/org/openmbee/mms/ldap/LdapSecurityConfig.java
                    userOptional = Optional.of(newUser);
                }

                User user = userOptional.get();
                if (user.getModified().isBefore(Instant.now().minus(userAttributesUpdate, ChronoUnit.HOURS))) {
                    parseLdapUser(userData, user);
                }
                user.setPassword(null);

                StringBuilder userDnBuilder = new StringBuilder();
                userDnBuilder.append(userData.getDn().toString());
                if (providerBase != null && !providerBase.isEmpty()) {
                    userDnBuilder.append(',');
                    userDnBuilder.append(providerBase);
                }
                String userDn = userDnBuilder.toString();

                List<Group> definedGroups = getGroupRepo().findAll();
                OrFilter orFilter = new OrFilter();

                for (Group definedGroup : definedGroups) {
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

                Set<Group> addGroups = new HashSet<>();

                for (String memberGroup : memberGroups) {
                    Optional<Group> group = getGroupRepo().findByName(memberGroup);
                    group.ifPresent(addGroups::add);
                }
<<<<<<< HEAD:ldap/src/main/java/org/openmbee/mms/ldap/security/LdapAuthoritiesConfig.java
=======

                if (logger.isDebugEnabled()) {
                    if ((long) addGroups.size() > 0) {
                        addGroups.forEach(group -> {
                            logger.debug("Group received: {}", group.getName());
                        });
                    } else {
                        logger.debug("No configured groups returned from LDAP");
                    }
                }

                user.setGroups(addGroups);
                userRepository.save(user);

>>>>>>> 376ac8b4b6003b4d39619d17561dcc10ab42a61c:ldap/src/main/java/org/openmbee/mms/ldap/LdapSecurityConfig.java
                List<GrantedAuthority> auths = AuthorityUtils
                    .createAuthorityList(addGroups.stream().map(Group::getName).distinct().toArray(String[]::new));
                if (user.isAdmin()) {
                    auths.add(new SimpleGrantedAuthority(AuthorizationConstants.MMSADMIN));
                }
                auths.add(new SimpleGrantedAuthority(AuthorizationConstants.EVERYONE));

                return auths;
            }
        }

        return new CustomLdapAuthoritiesPopulator(baseContextSource);

    }

    @Bean
    public AuthenticationProvider activeDirectoryLdapAuthenticationProvider() {
        ActiveDirectoryLdapAuthenticationProvider provider = new ActiveDirectoryLdapAuthenticationProvider(adDomain, providerUrl, providerBase);

        Hashtable<String, Object> env = new Hashtable<>();
        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        env.put(Context.PROVIDER_URL, providerUrl);
        env.put(Context.SECURITY_AUTHENTICATION, "simple");
        env.put(Context.SECURITY_PRINCIPAL, providerUserDn);
        env.put(Context.SECURITY_CREDENTIALS, providerPassword);

        provider.setContextEnvironmentProperties(env);

        provider.setSearchFilter(userSearchFilter);
        provider.setConvertSubErrorCodesToExceptions(true);
        provider.setUseAuthenticationRequestCredentials(true);
        return provider;
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
    public void changeUserPassword(String username, String password, boolean asAdmin) {
            throw new ForbiddenException("Cannot change or set passwords for external users.");
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

    public void parseLdapUser(DirContextOperations userData, User saveUser) {
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
    }

<<<<<<< HEAD:ldap/src/main/java/org/openmbee/mms/ldap/security/LdapAuthoritiesConfig.java
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
=======
    private User createLdapUser(DirContextOperations userData) {
        String username = userData.getStringAttribute(userAttributesUsername);
        logger.debug("Creating user for {} using LDAP", username);
        User user = saveLdapUser(userData, new User());
        user.setUsername(username);
        user.setEnabled(true);
        user.setAdmin(false);
        userRepository.save(user);

        return user;
>>>>>>> 376ac8b4b6003b4d39619d17561dcc10ab42a61c:ldap/src/main/java/org/openmbee/mms/ldap/LdapSecurityConfig.java
    }
}
