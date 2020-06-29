package org.openmbee.sdvc.localuser.config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openmbee.sdvc.localuser.security.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@PropertySource("classpath:application.properties")
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class LocalUserSecurityConfig {

    private static Logger logger = LogManager.getLogger(LocalUserSecurityConfig.class);
    @Autowired
    public UserDetailsServiceImpl userDetailsService;
    @Value("${sdvc.admin.username}")
    private String adminUsername;
    @Value("${sdvc.admin.password}")
    private String adminPassword;

    public LocalUserSecurityConfig() {
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(){
            //Turn off warnings for null/empty passwords
            @Override
            public boolean matches(CharSequence rawPassword, String encodedPassword) {
                if (encodedPassword == null || encodedPassword.length() == 0) {
                    return false;
                }
                return super.matches(rawPassword, encodedPassword);
            }
        };
    }

    @Autowired
    public void configureDaoAuth(AuthenticationManagerBuilder auth) {
        auth.authenticationProvider(authenticationProvider());
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        try {
            userDetailsService.loadUserByUsername(adminUsername);
        } catch (UsernameNotFoundException e) {
            //userDetailsService.setPasswordEncoder(passwordEncoder());
            userDetailsService.register(adminUsername, adminPassword, true);
            logger.info(String.format("Creating root user: %s with specified password.",
                adminUsername));
        }
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

}
