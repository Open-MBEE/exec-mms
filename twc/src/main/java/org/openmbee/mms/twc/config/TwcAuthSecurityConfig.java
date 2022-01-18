package org.openmbee.mms.twc.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.openmbee.mms.twc.security.TwcAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
@EnableWebSecurity
@EnableTransactionManagement
public class TwcAuthSecurityConfig {
    private static Logger logger = LoggerFactory.getLogger(TwcAuthSecurityConfig.class);

    public void setAuthConfig(HttpSecurity http) throws Exception {
        http.addFilterBefore(twcAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
    }

    @Bean
    public TwcAuthenticationFilter twcAuthenticationFilter() throws Exception {
        return new TwcAuthenticationFilter();
    }
}
