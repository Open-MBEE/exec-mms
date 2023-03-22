package org.openmbee.mms.ldap.config;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

import org.openmbee.mms.core.config.AuthorizationConstants;
import org.openmbee.mms.data.domains.global.Group;
import org.openmbee.mms.ldap.security.LdapAuthoritiesConfig;
import org.openmbee.mms.rdb.repositories.GroupRepository;
import org.openmbee.mms.rdb.repositories.UserRepository;
import org.openmbee.mms.data.domains.global.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.ldap.core.support.BaseLdapPathContextSource;
import org.springframework.ldap.filter.*;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configurers.ldap.LdapAuthenticationProviderConfigurer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.ldap.DefaultSpringSecurityContextSource;
import org.springframework.security.ldap.SpringSecurityLdapTemplate;
import org.springframework.security.ldap.authentication.ad.ActiveDirectoryLdapAuthenticationProvider;
import org.springframework.security.ldap.userdetails.LdapAuthoritiesPopulator;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.naming.Context;

@Configuration
@Conditional(LdapCondition.class)
@EnableTransactionManagement
public class LdapSecurityConfig {

    private final Logger logger = LoggerFactory.getLogger(LdapSecurityConfig.class);

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

    @Value("#{'${ldap.user.dn.pattern:uid={0}}'.split(';')}")
    private List<String> userDnPattern;

    @Value("${ldap.user.search.base:#{''}}")
    private String userSearchBase;

    @Value("${ldap.group.search.base:#{''}}")
    private String groupSearchBase;

    @Value("${ldap.group.role.attribute:cn}")
    private String groupRoleAttribute;

    @Value("${ldap.group.search.filter:(uniqueMember={0})}")
    private String groupSearchFilter;

    @Value("${ldap.user.search.filter:(uid={0})}")
    private String userSearchFilter;

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





//    private User createLdapUser(DirContextOperations userData) {
//        User user = saveLdapUser(userData, new User());
//        user.setUsername(userData.getStringAttribute(userAttributesUsername));
//        user.setEnabled(true);
//        user.setAdmin(false);
//        userRepository.save(user);
//
//
//        return user;
//    }


}