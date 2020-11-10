package org.openmbee.mms.authenticator.security;

import org.springframework.security.authentication.AbstractAuthenticationToken;

public class JwtAuthenticationToken extends AbstractAuthenticationToken {

    private String token;

    public JwtAuthenticationToken(String authToken) {
        super(null);
        this.token = authToken;
    }

    public String getAuthToken() {
        return token;
    }

    public void setAuthToken(String authToken) {
        this.token = authToken;
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return null;
    }
}
