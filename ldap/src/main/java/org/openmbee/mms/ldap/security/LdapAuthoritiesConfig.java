package org.openmbee.mms.ldap.security;

import org.openmbee.mms.core.config.AuthorizationConstants;
import org.openmbee.mms.core.exceptions.ForbiddenException;
import org.openmbee.mms.data.domains.global.Group;
import org.openmbee.mms.data.domains.global.User;
import org.openmbee.mms.users.objects.UsersCreateRequest;
import org.openmbee.mms.users.security.AbstractUsersDetailsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.ldap.core.support.BaseLdapPathContextSource;
import org.springframework.ldap.filter.AndFilter;
import org.springframework.ldap.filter.EqualsFilter;
import org.springframework.ldap.filter.HardcodedFilter;
import org.springframework.ldap.filter.OrFilter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.ldap.DefaultSpringSecurityContextSource;
import org.springframework.security.ldap.SpringSecurityLdapTemplate;
import org.springframework.security.ldap.userdetails.LdapAuthoritiesPopulator;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Configuration
public class LdapAuthoritiesConfig extends AbstractUsersDetailsService {

    private static Logger logger = LoggerFactory.getLogger(LdapAuthoritiesConfig.class);

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

            SpringSecurityLdapTemplate ldapTemplate;

            private CustomLdapAuthoritiesPopulator(BaseLdapPathContextSource ldapContextSource) {
                ldapTemplate = new SpringSecurityLdapTemplate(ldapContextSource);
            }

            @Override
            public Collection<? extends GrantedAuthority> getGrantedAuthorities(
                DirContextOperations userData, String username) {
                logger.debug("Populating authorities using LDAP");
                Optional<User> userOptional = getUserRepo().findByUsername(username);

                if (userOptional.isEmpty()) {
                    User newUser = register(parseLdapRegister(userData));

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
                    groupSearchFilter.replace("{0}", userDn));
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
    public BaseLdapPathContextSource contextSource() {
        DefaultSpringSecurityContextSource contextSource = new DefaultSpringSecurityContextSource(
            providerUrl);
        contextSource.setUserDn(providerUserDn);
        contextSource.setPassword(providerPassword);
        contextSource.setBase(providerBase);
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
}