package org.openmbee.sdvc.ldap;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchControls;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openmbee.sdvc.authenticator.config.SecurityConfig;
import org.openmbee.sdvc.data.domains.global.Group;
import org.openmbee.sdvc.rdb.repositories.GroupRepository;
import org.openmbee.sdvc.rdb.repositories.UserRepository;
import org.openmbee.sdvc.data.domains.global.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.ContextSource;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.ldap.core.support.BaseLdapPathContextSource;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.ldap.DefaultSpringSecurityContextSource;
import org.springframework.security.ldap.SpringSecurityLdapTemplate;
import org.springframework.security.ldap.userdetails.LdapAuthoritiesPopulator;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@PropertySource("classpath:application.properties")
@EnableGlobalMethodSecurity(prePostEnabled = true)
@EnableTransactionManagement
class LdapSecurityConfig extends SecurityConfig {

    private static Logger logger = LogManager.getLogger(SecurityConfig.class);

    @Value("${ldap.provider.url}")
    private String providerUrl;

    @Value("${ldap.provider.userdn}")
    private String providerUserDn;

    @Value("${ldap.provider.password}")
    private String providerPassword;

    @Value("${ldap.provider.base}")
    private String providerBase;

    @Value("${ldap.user.dn.pattern}")
    private String userDnPattern;

    @Value("${ldap.user.attributes.username}")
    private String userAttributesUsername;

    @Value("${ldap.user.attributes.email}")
    private String userAttributesEmail;

    @Value("${ldap.group.search.base}")
    private String groupSearchBase;

    @Value("${ldap.group.role.attribute}")
    private String groupRoleAttribute;

    @Value("${ldap.group.search.filter}")
    private String groupSearchFilter;

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
    public void configureLdapAuth(AuthenticationManagerBuilder auth) throws Exception {
        /*
            see this article : https://spring.io/guides/gs/authenticating-ldap/
            We  redefine our own LdapAuthoritiesPopulator which need ContextSource().
            We need to delegate the creation of the contextSource out of the builder-configuration.
        */
        auth.ldapAuthentication()
            .userDnPatterns(userDnPattern)
            .groupSearchBase(groupSearchBase)
            .groupRoleAttribute(groupRoleAttribute)
            .groupSearchFilter(groupSearchFilter)
            .rolePrefix("")
            .ldapAuthoritiesPopulator(ldapAuthoritiesPopulator())
            .contextSource(contextSource());
    }

    @Bean
    LdapAuthoritiesPopulator ldapAuthoritiesPopulator() {

        /*
          Specificity here : we don't get the Role by reading the members of available groups (which is implemented by
          default in Spring security LDAP), but we retrieve the groups from the field memberOf of the user.
         */
        class CustomLdapAuthoritiesPopulator implements LdapAuthoritiesPopulator {

            private final String[] GROUP_ATTRIBUTE = {groupRoleAttribute};
            SpringSecurityLdapTemplate ldapTemplate;

            private CustomLdapAuthoritiesPopulator(ContextSource contextSource) {
                ldapTemplate = new SpringSecurityLdapTemplate(contextSource);
            }

            private CustomAttributeMapper mapper = new CustomAttributeMapper();
            class CustomAttributeMapper implements AttributesMapper<GrantedAuthority> {
                @Override
                public GrantedAuthority mapFromAttributes(Attributes attributes) throws NamingException {
                    return new SimpleGrantedAuthority(attributes.get(groupRoleAttribute).get().toString());
                }
            }

            @Override
            public Collection<? extends GrantedAuthority> getGrantedAuthorities(
                DirContextOperations userData,
                String username) {
                //this only gets groups that's relevant in the db, otherwise can just use the ldap config to find all groups
                Optional<User> user = userRepository.findByUsername(username);
                if (!user.isPresent()) {
                    User newUser = new User();
                    newUser.setEmail(userData.getStringAttribute(userAttributesEmail));
                    newUser.setUsername(userData.getStringAttribute(userAttributesUsername));
                    newUser.setEnabled(true);
                    userRepository.save(newUser);
                }
                List<Group> groups = groupRepository.findAll();
                List<String> groupNames = groups.stream().map(group -> "(" + groupRoleAttribute + "=" + group.getName() + ")").collect(Collectors.toList());
                String userDn = userData.getDn().toString() + "," + providerBase;
                String groupFilter = "(&(uniqueMember=" + userDn + ")(|" + String.join("", groupNames) + "))";
                List<GrantedAuthority> gas = ldapTemplate.search("", groupFilter, SearchControls.SUBTREE_SCOPE, GROUP_ATTRIBUTE, mapper);
                return gas;
            }
        }

        return new CustomLdapAuthoritiesPopulator(contextSource());

    }

    @Bean
    public BaseLdapPathContextSource contextSource() {
        DefaultSpringSecurityContextSource contextSource = new DefaultSpringSecurityContextSource(
            providerUrl);
        contextSource.setUserDn(providerUserDn);
        contextSource.setPassword(providerPassword);
        return contextSource;
    }

}
