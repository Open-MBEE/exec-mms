package org.openmbee.mms.authenticator.controllers;

import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.openmbee.mms.authenticator.security.JwtAuthenticationResponse;
import org.openmbee.mms.authenticator.security.JwtTokenGenerator;
import org.openmbee.mms.authenticator.security.JwtTokenValidationResponse;
import org.openmbee.mms.authenticator.security.JwtAuthenticationRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Auth")
public class AuthenticationController {

    private AuthenticationManager authenticationManager;
    private JwtTokenGenerator jwtTokenUtil;

    @Autowired
    public AuthenticationController(AuthenticationManager authenticationManager,
        JwtTokenGenerator jwtTokenUtil) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenUtil = jwtTokenUtil;
    }

    @PostMapping(value = "/authentication", consumes = MediaType.APPLICATION_JSON_VALUE)
    @SecurityRequirements(value = {})
    public JwtAuthenticationResponse createAuthenticationToken(
        @RequestBody
            JwtAuthenticationRequest authenticationRequest) {

        final Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(authenticationRequest.getUsername(),
                authenticationRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        final UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        final String token = jwtTokenUtil.generateToken(userDetails);
        return new JwtAuthenticationResponse(token);

    }

    @GetMapping(value = "/authentication")
    @PreAuthorize("isAuthenticated()")
    public JwtAuthenticationResponse getAuthenticationToken() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            final UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            final String token = jwtTokenUtil.generateToken(userDetails);
            return new JwtAuthenticationResponse(token);
        }
        return new JwtAuthenticationResponse(null);
    }


    @GetMapping(value = "/checkAuth")
    @PreAuthorize("isAuthenticated()")
    public JwtTokenValidationResponse checkAuthenticationToken() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (! (authentication instanceof AnonymousAuthenticationToken)) {
            return new JwtTokenValidationResponse(authentication.getName());
        }
        return new JwtTokenValidationResponse(null);
    }

}
