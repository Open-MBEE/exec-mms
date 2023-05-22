package org.openmbee.mms.users.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.openmbee.mms.core.config.AuthorizationConstants;
import org.openmbee.mms.core.exceptions.BadRequestException;
import org.openmbee.mms.core.exceptions.NotFoundException;
import org.openmbee.mms.core.exceptions.UnauthorizedException;
import org.openmbee.mms.core.utils.AuthenticationUtils;
import org.openmbee.mms.json.UserJson;
import org.openmbee.mms.users.security.UserDetails;
import org.openmbee.mms.users.security.UserDetailsService;
import org.openmbee.mms.users.objects.UsersCreateRequest;
import org.openmbee.mms.users.objects.UsersResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@RestController
@Tag(name = "Auth")
public class UsersController {

    private UserDetailsService userDetailsService;

    @Autowired
    public void setUserDetailsService(@Qualifier("") UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @PostMapping(value = "/users", consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize(AuthorizationConstants.IS_MMSADMIN)
    public UsersResponse createOrUpdateUser(@RequestBody UsersCreateRequest req) {
        UsersResponse res = new UsersResponse();
        List<UserJson> users = new ArrayList<>();
        UserDetails userDetails;
        try {
            userDetails = userDetailsService.loadUserByUsername(req.getUsername());
        } catch (UsernameNotFoundException e) {
            users.add(userDetailsService.register(req));
            res.setUsers(users);
            return res;
        }
        users.add(userDetailsService.update(req, userDetails.getUser()));
        res.setUsers(users);
        return res;
    }

    @GetMapping(value = "/users")
    @PreAuthorize("isAuthenticated()")
    public UsersResponse getUsers() {
        UsersResponse res = new UsersResponse();
        Collection<UserJson> users = userDetailsService.getUsers();
        res.setUsers(users);
        return res;
    }

    @GetMapping(value = "/users/:username")
    @PreAuthorize("isAuthenticated()")
    public UsersResponse getUsers(@PathVariable String username) {
        UsersResponse res = new UsersResponse();
        Collection<UserJson> users = new ArrayList<>();
        users.add(userDetailsService.loadUserByUsername(username).getUser());
        res.setUsers(users);
        return res;
    }

    @GetMapping(value = "/whoami")
    @PreAuthorize("isAuthenticated()")
    public UsersResponse getCurrentUser() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String user = authentication.getName();
        UsersResponse res = new UsersResponse();
        Collection<UserJson> users = new ArrayList<>();
        users.add(userDetailsService.loadUserByUsername(user).getUser());
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
                userDetailsService.changeUserPassword(req.getUsername(), req.getPassword(), requesterAdmin);
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
