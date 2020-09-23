package org.openmbee.sdvc.localuser.config;

import org.openmbee.sdvc.localuser.security.UserDetailsServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AuthProviderConfig {

    private static Logger logger = LoggerFactory.getLogger(LocalUserSecurityConfig.class);

    private UserDetailsServiceImpl userDetailsService;
    private PasswordEncoder passwordEncoder;

    @Value("${sdvc.admin.username}")
    private String adminUsername;
    @Value("${sdvc.admin.password}")
    private String adminPassword;

    @Autowired
    public void setUserDetailsService(UserDetailsServiceImpl userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Autowired
    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider() {
        try {
            userDetailsService.loadUserByUsername(adminUsername);
        } catch (UsernameNotFoundException e) {
            userDetailsService.register(adminUsername, adminPassword, true);
            logger.info(String.format("Creating root user: %s with specified password.",
                adminUsername));
        }
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder);
        return authProvider;
    }
}
