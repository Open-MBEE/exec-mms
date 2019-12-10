package org.openmbee.sdvc.authenticator.controllers;

import org.openmbee.sdvc.authenticator.security.JwtAuthenticationRequest;
import org.openmbee.sdvc.authenticator.security.JwtAuthenticationResponse;
import org.openmbee.sdvc.authenticator.security.JwtTokenGenerator;
import org.openmbee.sdvc.authenticator.security.UserDetailsImpl;
import org.openmbee.sdvc.authenticator.services.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthenticationController {

    private AuthenticationManager authenticationManager;
    private UserDetailsServiceImpl userDetailsService;
    private JwtTokenGenerator jwtTokenUtil;

    @Autowired
    public AuthenticationController(AuthenticationManager authenticationManager,
        UserDetailsServiceImpl userDetailsService, JwtTokenGenerator jwtTokenUtil) {
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.jwtTokenUtil = jwtTokenUtil;
    }

    @PostMapping(value = "/authentication", consumes = MediaType.APPLICATION_JSON_VALUE)
    public JwtAuthenticationResponse createAuthenticationToken(
        @RequestBody
            JwtAuthenticationRequest authenticationRequest) {

        final Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(authenticationRequest.getUsername(),
                authenticationRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        final UserDetailsImpl userDetails = userDetailsService
            .loadUserByUsername(authenticationRequest.getUsername());
        final String token = jwtTokenUtil.generateToken(userDetails);
        return new JwtAuthenticationResponse(token);

    }

    @PostMapping(value = "/user", consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("authentication.name == 'admin'")
    public Object createUser(@RequestBody JwtAuthenticationRequest req) {
        try {
            userDetailsService.loadUserByUsername(req.getUsername());
        } catch (UsernameNotFoundException e) {
            userDetailsService.register(req.getUsername(), req.getPassword());
        }
        return "";
    }
}
