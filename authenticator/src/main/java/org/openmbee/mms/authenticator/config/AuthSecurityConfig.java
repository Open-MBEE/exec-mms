package org.openmbee.mms.authenticator.config;

import org.openmbee.mms.authenticator.security.JwtAuthenticationEntryPoint;
import org.openmbee.mms.authenticator.security.JwtAuthenticationProvider;
import org.openmbee.mms.authenticator.security.JwtAuthenticationTokenFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class AuthSecurityConfig {

    private static Logger logger = LoggerFactory.getLogger(AuthSecurityConfig.class);

    @Autowired
    public void setAuthProvider(AuthenticationManagerBuilder auth,
            JwtAuthenticationProvider provider) {
        auth.authenticationProvider(provider);
    }

    public void setAuthConfig(HttpSecurity http) throws Exception {
        http.exceptionHandling()
            .authenticationEntryPoint(new JwtAuthenticationEntryPoint()).and()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        http.addFilterBefore(jwtAuthenticationTokenFilter(), UsernamePasswordAuthenticationFilter.class);
    }

    @Bean
    public JwtAuthenticationTokenFilter jwtAuthenticationTokenFilter() {
        return new JwtAuthenticationTokenFilter();
    }
}
