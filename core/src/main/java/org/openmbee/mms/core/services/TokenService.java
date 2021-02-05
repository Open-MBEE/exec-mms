package org.openmbee.mms.core.services;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

public interface TokenService {
    String generateToken(UserDetails userDetails);
    String generateToken(String principal, Collection<String> authorities);
}
