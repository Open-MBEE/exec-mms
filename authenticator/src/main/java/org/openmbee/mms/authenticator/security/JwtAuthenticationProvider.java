package org.openmbee.mms.authenticator.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

@Component
public class JwtAuthenticationProvider implements AuthenticationProvider {

    private JwtTokenGenerator jwtTokenGenerator;

    @Autowired
    public void setJwtTokenGenerator(JwtTokenGenerator jwtTokenGenerator) {
        this.jwtTokenGenerator = jwtTokenGenerator;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        JwtAuthenticationToken token = (JwtAuthenticationToken)authentication;
        String authToken = token.getAuthToken();
        String username = jwtTokenGenerator.getUsernameFromToken(authToken);
        if (username != null && jwtTokenGenerator.validateToken(authToken)) {
            return new UsernamePasswordAuthenticationToken(
                username, null, jwtTokenGenerator.getAuthoritiesFromToken(authToken));
        }
        return null;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return (JwtAuthenticationToken.class.isAssignableFrom(authentication));
    }
}
