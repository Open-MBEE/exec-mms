package org.openmbee.sdvc.ldap;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.naming.ldap.LdapName;
import javax.naming.ldap.Rdn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openmbee.sdvc.authenticator.config.AuthSecurityConfig;
import org.openmbee.sdvc.rdb.repositories.UserRepository;
import org.openmbee.sdvc.data.domains.global.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.ldap.core.ContextSource;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.ldap.core.support.BaseLdapPathContextSource;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.ldap.DefaultSpringSecurityContextSource;
import org.springframework.security.ldap.SpringSecurityLdapTemplate;
import org.springframework.security.ldap.userdetails.LdapAuthoritiesPopulator;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@PropertySource("classpath:application.properties")
@EnableGlobalMethodSecurity(prePostEnabled = true)
@EnableTransactionManagement
public abstract class LdapSecurityConfig extends AuthSecurityConfig {

    private static Logger logger = LogManager.getLogger(AuthSecurityConfig.class);

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

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Autowired
    public void configureLdapAuth(AuthenticationManagerBuilder auth) throws Exception {
        /*
            see this article : https://spring.io/guides/gs/authenticating-ldap/
            We  redefine our own LdapAuthoritiesPopulator which need ContextSource().
            We need to delegate the creation of the contextSource out of the builder-configuration.
        */
        auth.ldapAuthentication().userDnPatterns(userDnPattern).groupSearchBase(groupSearchBase)
            .groupRoleAttribute(groupRoleAttribute).groupSearchFilter(groupSearchFilter)
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

            private final String[] GROUP_ATTRIBUTE = {"cn", "uniqueMember"};
            SpringSecurityLdapTemplate ldapTemplate;

            private CustomLdapAuthoritiesPopulator(ContextSource contextSource) {
                ldapTemplate = new SpringSecurityLdapTemplate(contextSource);
            }

            @Override
            public Collection<? extends GrantedAuthority> getGrantedAuthorities(
                DirContextOperations userData,
                String username) {
                Optional<User> user = userRepository.findByUsername(username);
                if (!user.isPresent()) {
                    User newUser = new User();
                    newUser.setEmail(userData.getStringAttribute(userAttributesEmail));
                    newUser.setUsername(userData.getStringAttribute(userAttributesUsername));
                    newUser.setEnabled(true);
                    userRepository.save(newUser);
                }

                String[] groupDns = new String[]{groupSearchBase};

                String roles = Stream.of(groupDns).map(groupDn -> {
                    LdapName groupLdapName = (LdapName) ldapTemplate
                        .retrieveEntry(groupDn, GROUP_ATTRIBUTE).getDn();
                    // split DN in its different components et get only the last one (cn=my_group)
                    // getValue() allows to only get get the value of the pair (cn=>my_group)
                    return groupLdapName.getRdns().stream().map(Rdn::getValue).reduce((a, b) -> b)
                        .orElse(null);
                }).map(x -> (String) x).collect(Collectors.joining(","));

                return AuthorityUtils.commaSeparatedStringToAuthorityList(roles);
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
