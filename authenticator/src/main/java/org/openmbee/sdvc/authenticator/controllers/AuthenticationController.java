package org.openmbee.sdvc.authenticator.controllers;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import org.openmbee.sdvc.authenticator.security.*;
import org.openmbee.sdvc.core.config.AuthorizationConstants;
import org.openmbee.sdvc.core.exceptions.NotFoundException;
import org.openmbee.sdvc.core.exceptions.UnauthorizedException;
import org.openmbee.sdvc.core.utils.AuthenticationUtils;
import org.openmbee.sdvc.authenticator.security.JwtAuthenticationRequest;
import org.openmbee.sdvc.authenticator.security.JwtAuthenticationResponse;
import org.openmbee.sdvc.authenticator.security.JwtTokenGenerator;
import org.openmbee.sdvc.authenticator.security.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
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

    @PostMapping(value = "/user", consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize(AuthorizationConstants.IS_MMSADMIN)
    public Object createUser(@RequestBody JwtAuthenticationRequest req) {
        //TODO allow create admin accounts
        //TODO should allow admin authority string to be set via properties
        try {
            userDetailsService.loadUserByUsername(req.getUsername());
        } catch (UsernameNotFoundException e) {
            userDetailsService.register(req.getUsername(), req.getPassword(), false);
        }
        return "";
    }

    @PostMapping(value = "/password", consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("isAuthenticated()")
    public Object updatePassword(@RequestBody JwtAuthenticationRequest req,
                                 @Parameter(hidden = true) Authentication auth) {
        final String requester = auth.getName();
        final boolean requesterAdmin = AuthenticationUtils.hasGroup(auth, AuthorizationConstants.MMSADMIN);

        try {
            if(requesterAdmin || requester.equals(req.getUsername())){
                userDetailsService.changeUserPassword(req.getUsername(), req.getPassword());
            } else {
                throw new UnauthorizedException("Not authorized");
            }

        } catch (UsernameNotFoundException e) {
            if(requesterAdmin) {
                throw new NotFoundException("User not found");
            } else {
                throw new UnauthorizedException("Not authorized");
            }
        }
        return "";
    }

}
