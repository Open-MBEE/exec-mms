package org.openmbee.mms.users.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.openmbee.mms.core.config.AuthorizationConstants;
import org.openmbee.mms.core.exceptions.BadRequestException;
import org.openmbee.mms.core.exceptions.NotFoundException;
import org.openmbee.mms.core.exceptions.UnauthorizedException;
import org.openmbee.mms.core.utils.AuthenticationUtils;
import org.openmbee.mms.data.domains.global.User;
import org.openmbee.mms.users.security.UsersDetailsService;
import org.openmbee.mms.users.objects.UsersCreateRequest;
import org.openmbee.mms.users.objects.UsersResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@Tag(name = "Auth")
public class UsersController {

    private UsersDetailsService usersDetailsService;

    @Autowired
    public UsersController(UsersDetailsService usersDetailsService) {
        this.usersDetailsService = usersDetailsService;
    }

    @PostMapping(value = "/user", consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize(AuthorizationConstants.IS_MMSADMIN)
    public UsersCreateRequest createUser(@RequestBody UsersCreateRequest req) {
        try {
            usersDetailsService.loadUserByUsername(req.getUsername());
        } catch (UsernameNotFoundException e) {
            usersDetailsService.register(req);
            return req;
        }
        throw new BadRequestException("User already exists");
    }

    @GetMapping(value = "/users")
    @PreAuthorize("isAuthenticated()")
    public UsersResponse getUsers(@RequestParam(required = false) String user) {
        UsersResponse res = new UsersResponse();
        List<User> users = new ArrayList<>();
        if (user != null) {
            users.add(usersDetailsService.loadUserByUsername(user).getUser());
        } else {
            users = usersDetailsService.getUsers();
        }
        res.setUsers(users);
        return res;
    }

    @GetMapping(value = "/whoami")
    @PreAuthorize("isAuthenticated()")
    public UsersResponse getCurrentUser() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String user = authentication.getName();
        UsersResponse res = new UsersResponse();
        List<User> users = new ArrayList<>();
        users.add(usersDetailsService.loadUserByUsername(user).getUser());
        res.setUsers(users);
        return res;
    }

    @PostMapping(value = "/password", consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("isAuthenticated()")
    public Object updatePassword(@RequestBody UsersCreateRequest req,
        Authentication auth) {
        final String requester = auth.getName();
        final boolean requesterAdmin = AuthenticationUtils
            .hasGroup(auth, AuthorizationConstants.MMSADMIN);

        try {
            if (requesterAdmin || requester.equals(req.getUsername())) {
                usersDetailsService.changeUserPassword(req.getUsername(), req.getPassword(), requesterAdmin);
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
