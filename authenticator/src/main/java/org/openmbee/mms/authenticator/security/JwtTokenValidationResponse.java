package org.openmbee.mms.authenticator.security;

import java.io.Serializable;

public class JwtTokenValidationResponse implements Serializable {
    private static final long serialVersionUID = 1L;
    private final String username;

    public JwtTokenValidationResponse(String username) {
        this.username = username;
    }

    public String getUsername() {
        return this.username;
    }
}
