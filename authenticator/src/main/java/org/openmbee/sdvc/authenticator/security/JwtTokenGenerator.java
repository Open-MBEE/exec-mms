package org.openmbee.sdvc.authenticator.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.crypto.SecretKey;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenGenerator implements Serializable {

    private static final long serialVersionUID = 6463567580980594813L;
    private static final String CLAIM_KEY_USERNAME = "sub";
    private static final String CLAIM_KEY_USERID = "id";
    private static final String CLAIM_KEY_CREATED = "created";
    private static final String CLAIM_KEY_ENABLED = "enabled";
    static Logger logger = LogManager.getLogger(JwtTokenGenerator.class);
    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long expiration;

    public String getUsernameFromToken(String token) {
        String email = null;
        try {
            final Claims claims = getClaimsFromToken(token);
            if (claims != null) {
                email = claims.getSubject();
            }
        } catch (Exception e) {
            if (logger.isDebugEnabled()) {
                logger.debug("Error getting username from token", e);
            }
        }
        return email;
    }

    public Date getCreatedDateFromToken(String token) {
        Date created = null;
        try {
            final Claims claims = getClaimsFromToken(token);
            if (claims != null) {
                created = new Date((Long) claims.get(CLAIM_KEY_CREATED));
            }
        } catch (Exception e) {
            if (logger.isDebugEnabled()) {
                logger.debug("Error getting created date from token", e);
            }
        }
        return created;
    }

    public Date getExpirationDateFromToken(String token) {
        Date expires = null;
        try {
            final Claims claims = getClaimsFromToken(token);
            if (claims != null) {
                expires = claims.getExpiration();
            }
        } catch (Exception e) {
            if (logger.isDebugEnabled()) {
                logger.debug("Error getting expiration from token", e);
            }
        }
        return expires;
    }

    private Claims getClaimsFromToken(String token) {
        Claims claims = null;
        try {
            claims = Jwts.parser().setSigningKey(getSecretKey()).parseClaimsJws(token).getBody();
        } catch (Exception e) {
            if (logger.isDebugEnabled()) {
                logger.debug("Error getting claims from token", e);
            }
        }
        return claims;
    }

    private Date generateExpirationDate() {
        return new Date(System.currentTimeMillis() + expiration * 1000);
    }

    private Boolean isTokenExpired(String token) {
        final Date expirationDate = getExpirationDateFromToken(token);
        return expirationDate.before(new Date());
    }

    public String generateToken(UserDetails userDetails) {
        //can also put in authorities here
        Map<String, Object> claims = new HashMap<>();
        claims.put(CLAIM_KEY_USERNAME, userDetails.getUsername());
        claims.put(CLAIM_KEY_USERID, userDetails.getUsername());
        claims.put(CLAIM_KEY_ENABLED, userDetails.isEnabled());
        claims.put(CLAIM_KEY_CREATED, new Date());
        return generateToken(claims);
    }

    private String generateToken(Map<String, Object> claims) {
        return Jwts.builder().setClaims(claims).setExpiration(generateExpirationDate())
            .signWith(getSecretKey())
            .compact();
    }

    private SecretKey getSecretKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    public String refreshToken(String token) {
        String refreshedToken = null;
        try {
            final Claims claims = getClaimsFromToken(token);
            if (claims != null) {
                claims.put(CLAIM_KEY_CREATED, new Date());
                refreshedToken = generateToken(claims);
            }
        } catch (Exception e) {
            if (logger.isDebugEnabled()) {
                logger.debug("Error refreshing token", e);
            }
        }
        return refreshedToken;
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        final String email = getUsernameFromToken(token);
        return (email.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

}
