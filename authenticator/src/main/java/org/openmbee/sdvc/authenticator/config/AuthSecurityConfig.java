package org.openmbee.sdvc.authenticator.config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openmbee.sdvc.authenticator.security.JwtAuthenticationEntryPoint;
import org.openmbee.sdvc.authenticator.security.JwtAuthenticationTokenFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
@EnableWebSecurity
@EnableTransactionManagement
public class AuthSecurityConfig {

    private static Logger logger = LogManager.getLogger(AuthSecurityConfig.class);

    public AuthSecurityConfig() {
    }

    public void setAuthConfig(HttpSecurity http) throws Exception {
        http.exceptionHandling()
            .authenticationEntryPoint(new JwtAuthenticationEntryPoint()).and()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        http.addFilterBefore(jwtAuthenticationTokenFilter(), UsernamePasswordAuthenticationFilter.class);
    }

    @Bean
    public JwtAuthenticationTokenFilter jwtAuthenticationTokenFilter() throws Exception {
        return new JwtAuthenticationTokenFilter();
    }
}
