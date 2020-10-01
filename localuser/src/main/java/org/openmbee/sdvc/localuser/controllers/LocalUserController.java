package org.openmbee.sdvc.localuser.controllers;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.openmbee.sdvc.core.config.AuthorizationConstants;
import org.openmbee.sdvc.core.exceptions.BadRequestException;
import org.openmbee.sdvc.core.exceptions.NotFoundException;
import org.openmbee.sdvc.core.exceptions.UnauthorizedException;
import org.openmbee.sdvc.core.utils.AuthenticationUtils;
import org.openmbee.sdvc.localuser.security.UserCreateRequest;
import org.openmbee.sdvc.localuser.security.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Auth")
public class LocalUserController {

    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    public LocalUserController(UserDetailsServiceImpl userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @PostMapping(value = "/user", consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize(AuthorizationConstants.IS_MMSADMIN)
    public UserCreateRequest createUser(@RequestBody UserCreateRequest req) {
        try {
            userDetailsService.loadUserByUsername(req.getUsername());
        } catch (UsernameNotFoundException e) {
            userDetailsService.register(req.getUsername(), req.getPassword(), req.isAdmin());
            return req;
        }
        throw new BadRequestException("User already exists");
    }

    @PostMapping(value = "/password", consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("isAuthenticated()")
    public Object updatePassword(@RequestBody UserCreateRequest req,
        Authentication auth) {
        final String requester = auth.getName();
        final boolean requesterAdmin = AuthenticationUtils
            .hasGroup(auth, AuthorizationConstants.MMSADMIN);

        try {
            if (requesterAdmin || requester.equals(req.getUsername())) {
                userDetailsService.changeUserPassword(req.getUsername(), req.getPassword());
            } else {
                throw new UnauthorizedException("Not authorized");
            }

        } catch (UsernameNotFoundException e) {
            if (requesterAdmin) {
                throw new NotFoundException("User not found");
            } else {
                throw new UnauthorizedException("Not authorized");
            }
        }
        return "";
    }

}
