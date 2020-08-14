package org.openmbee.sdvc.authenticator.security;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {

    @Autowired
    private JwtTokenGenerator jwtTokenGenerator;

    private final String AUTHORIZATION = "Authorization";
    private final String BEARER = "Bearer ";
    private final String TOKEN = "token";

    @Override
    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
        throws IOException, ServletException {

        String authHeader = request.getHeader(AUTHORIZATION);
        String[] bearerParam = request.getParameterMap().get(TOKEN);

        // Require the Authorization: Bearer format for auth header
        if (authHeader != null && authHeader.startsWith(BEARER)) {
            String authToken = request.getHeader(AUTHORIZATION).substring(BEARER.length());
            if (!authToken.isEmpty()) {
                authenticate(request, authToken);
            }
        } else if(bearerParam != null && bearerParam.length > 0) {
            authenticate(request, bearerParam[0]);
        }
        chain.doFilter(request, response);
    }

    private void authenticate(HttpServletRequest request, String authToken) {
        String username = jwtTokenGenerator.getUsernameFromToken(authToken);
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null && jwtTokenGenerator.validateToken(authToken)) {
            UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(username, null, jwtTokenGenerator.getAuthoritiesFromToken(authToken));
            authentication.setDetails(
                new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
    }
}
