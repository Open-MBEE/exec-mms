package org.openmbee.sdvc.twc.config;

import org.openmbee.sdvc.authenticator.config.AuthSecurityConfig;
import org.openmbee.sdvc.twc.security.TwcAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@PropertySource("classpath:application.properties")
@EnableGlobalMethodSecurity(prePostEnabled = true)
@EnableTransactionManagement
public abstract class TwcDelegatingSecurityConfig extends AuthSecurityConfig {

	@Override
	protected void configure(HttpSecurity http) throws Exception {
	    super.configure(http);
		http.csrf().disable();
		http.headers().frameOptions().disable();
		http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
		http.authorizeRequests().anyRequest().authenticated().and().httpBasic();
		http.addFilterBefore(twcAuthenticationFilter(), BasicAuthenticationFilter.class);
	}

	@Bean
	public TwcAuthenticationFilter twcAuthenticationFilter() throws Exception {
		return new TwcAuthenticationFilter(authenticationManager());
	}

	@Bean
    public TwcConfig twcConfig() throws Exception {
	    return new TwcConfig();
    }

}
