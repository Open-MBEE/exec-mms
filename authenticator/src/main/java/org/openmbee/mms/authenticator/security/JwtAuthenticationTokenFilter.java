package org.openmbee.mms.authenticator.security;

import java.io.IOException;
import java.util.Optional;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {

    private final String AUTHORIZATION = "Authorization";
    private final String BEARER = "Bearer ";
    private final String TOKEN = "token";

    private AuthenticationManager authManager;
    private WebAuthenticationDetailsSource detailsSource;

    @Autowired
    public void setAuthenticationManager(AuthenticationManager authenticationManager) {
        this.authManager = authenticationManager;
    }

    @Autowired
    public void setDetailsSource(Optional<WebAuthenticationDetailsSource> detailsSource) {
        this.detailsSource = detailsSource.isPresent() ? detailsSource.get() : new WebAuthenticationDetailsSource();
    }

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
        } else if (bearerParam != null && bearerParam.length > 0) {
            authenticate(request, bearerParam[0]);
        }
        chain.doFilter(request, response);
    }

    private void authenticate(HttpServletRequest request, String authToken) {
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            return;
        }
        JwtAuthenticationToken token = new JwtAuthenticationToken(authToken);
        token.setDetails(detailsSource.buildDetails(request));
        try {
            Authentication auth = authManager.authenticate(token);
            if (auth != null) {
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        } catch (Exception e) {
        }
    }
}
