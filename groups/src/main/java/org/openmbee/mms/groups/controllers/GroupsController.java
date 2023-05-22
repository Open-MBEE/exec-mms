package org.openmbee.mms.groups.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.openmbee.mms.core.config.AuthorizationConstants;
import org.openmbee.mms.core.config.Privileges;
import org.openmbee.mms.core.dao.GroupPersistence;
import org.openmbee.mms.core.dao.UserGroupsPersistence;
import org.openmbee.mms.core.exceptions.*;
import org.openmbee.mms.core.objects.*;
import org.openmbee.mms.core.security.MethodSecurityService;
import org.openmbee.mms.core.services.PermissionService;
import org.openmbee.mms.groups.constants.GroupConstants;
import org.openmbee.mms.groups.objects.*;
import org.openmbee.mms.groups.services.GroupValidationService;
import org.openmbee.mms.json.GroupJson;
import org.openmbee.mms.json.UserJson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/groups")
@Tag(name = "Groups")
public class GroupsController {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    protected ObjectMapper om;


    private GroupPersistence groupPersistence;
    private UserGroupsPersistence userGroupsPersistence;
    private GroupValidationService groupValidationService;

    protected PermissionService permissionService;

    protected MethodSecurityService mss;

    @Autowired
    public void setGroupPersistence(GroupPersistence groupPersistence) {
        this.groupPersistence = groupPersistence;
    }

    @Autowired
    public void setUserGroupsPersistence(UserGroupsPersistence userGroupsPersistence) {
        this.userGroupsPersistence = userGroupsPersistence;
    }

    @Autowired
    public void setGroupValidationService(GroupValidationService groupValidationService) {
        this.groupValidationService = groupValidationService;
    }

    @Autowired
    public void setPermissionService(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    @Autowired
    public void setMss(MethodSecurityService mss) {
        this.mss = mss;
    }

    @Autowired
    public void setObjectMapper(ObjectMapper om) {
        this.om = om;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("isAuthenticated()")
    public GroupsResponse createOrUpdateGroups(
        @RequestBody GroupsRequest groupPost,
        Authentication auth) {

        GroupsResponse response = new GroupsResponse();
        if (groupPost.getGroups().isEmpty()) {
            throw new BadRequestException(response.addMessage("No groups provided"));
        }

        for (GroupJson group : groupPost.getGroups()) {
            
            if (group.getName() == null || group.getName().isEmpty()) {
                group.setName(UUID.randomUUID().toString());
            }

            if (!groupValidationService.isValidGroupName(group.getName())) {
                throw new BadRequestException(GroupConstants.INVALID_GROUP_NAME);
            }

            if (group.getType() == null || group.getType().isEmpty()) {
                throw new BadRequestException(response.addMessage("No type provided for group:" + group.getName()));
            }

            Optional<GroupJson> optG = groupPersistence.findByName(group.getName());
            boolean newGroup = true;
            GroupJson g = new GroupJson();
            if (optG.isPresent()) {
                newGroup = false;
                g = optG.get();
                if (!mss.hasGroupPrivilege(auth, g.getName(), Privileges.GROUP_EDIT.name(), false)) {
                    response.addRejection(new Rejection(group, 403, GroupConstants.NO_PERMISSSION));
                    continue;
                }
                if (!group.getType().equals(g.getType()) && !(userGroupsPersistence.findUsersInGroup(group.getName()).isEmpty())) {
                    response.addRejection(new Rejection(group, 403, GroupConstants.GROUP_NOT_EMPTY));
                }
            }
            g.setName(group.getName());
            g.setType(group.getType());
            logger.info("Saving group: {}", g.getName());
            groupPersistence.save(g);
            if (newGroup) {
                permissionService.initGroupPerms(group.getName(), auth.getName());
            }
            response.getGroups().add(group);
        }
        if (groupPost.getGroups().size() == 1) {
            handleSingleResponse(response);
        }
        return response;
    }

    @GetMapping
    public GroupsResponse getAllGroups(
        @RequestParam(required = false) boolean users,
        Authentication auth) {

        GroupsResponse response = new GroupsResponse();
        Collection<GroupJson> allGroups = groupPersistence.findAll();
        for (GroupJson group : allGroups) {
            if (mss.hasGroupPrivilege(auth, group.getName(), Privileges.GROUP_READ.name(), false)) {

                if (users) {
                    Collection<UserJson> userSet = userGroupsPersistence.findUsersInGroup(group.getName());
                    Set<String> usernames = userSet.stream().map(UserJson::getUsername).collect(Collectors.toSet());
                    group.put("users", usernames);
                }
                response.getGroups().add(group);
            }
        }
        return response;
    }



    @GetMapping(value = "/{groupName}")
    @PreAuthorize("@mss.hasGroupPrivilege(authentication, #groupName, 'GROUP_READ', true)")
    public GroupResponse getGroup(
        @PathVariable String groupName,
        @RequestParam(required = false) boolean users
    ) {
        if (users) {
            return new GroupResponse(groupPersistence.findByName(groupName).orElseThrow(() -> new NotFoundException(GroupConstants.GROUP_NOT_FOUND)),
                userGroupsPersistence.findUsersInGroup(groupName).stream().map(UserJson::getUsername).collect(Collectors.toList()));
        }
        return new GroupResponse(groupPersistence.findByName(groupName).orElseThrow(() -> new NotFoundException(GroupConstants.GROUP_NOT_FOUND)));
    }

    @DeleteMapping("/{groupName}")
    @PreAuthorize("@mss.hasGroupPrivilege(authentication, #groupName, 'GROUP_DELETE', false)")
    @ResponseBody
    public void deleteLocalGroup(@PathVariable String groupName) {
        GroupJson groupObj = groupPersistence.findByName(groupName).orElseThrow(() -> new NotFoundException(GroupConstants.GROUP_NOT_FOUND));
        if (groupValidationService.isRestrictedGroup(groupObj.getName())) {
            throw new BadRequestException(GroupConstants.NO_DELETE_RESTRICTED);
        }
        if (groupValidationService.canDeleteGroup(groupObj)) {
            groupPersistence.delete(groupObj);
        } else {
            throw new BadRequestException(GroupConstants.GROUP_NOT_EMPTY);
        }
    }

    @GetMapping("/{groupName}/users")
    @PreAuthorize("@mss.hasGroupPrivilege(authentication, #groupName, 'GROUP_READ', true)")
    public GroupUsersResponse getAllGroupUsers( @PathVariable String groupName) {

        GroupUsersResponse response = new GroupUsersResponse();
        groupPersistence.findByName(groupName).orElseThrow(() -> new NotFoundException(GroupConstants.GROUP_NOT_FOUND));
        Set<String> usernames = userGroupsPersistence.findUsersInGroup(groupName).stream().map(UserJson::getUsername).collect(Collectors.toSet());
        response.getUsers().addAll(usernames);
        return response;
    }

    @PostMapping("/{groupName}/users")
    @PreAuthorize("@mss.hasGroupPrivilege(authentication, #groupName, 'GROUP_EDIT', true)")
    public GroupUpdateResponse updateGroupUsers(@PathVariable String groupName,
            @RequestBody GroupUpdateRequest groupUpdateRequest) {

        if (groupUpdateRequest.getAction() == null) {
            throw new BadRequestException(GroupConstants.INVALID_ACTION);
        }

        if (groupUpdateRequest.getUsers() == null ||
            groupUpdateRequest.getUsers().isEmpty()) {
            throw new BadRequestException(GroupConstants.NO_USERS_PROVIDED);
        }

        if (groupValidationService.isRestrictedGroup(groupName)) {
            throw new BadRequestException(GroupConstants.RESTRICTED_GROUP);
        }

        if(groupPersistence.findByName(groupName).isEmpty()) {
            throw new NotFoundException(GroupConstants.GROUP_NOT_FOUND);
        }

        GroupJson groupObj = groupPersistence.findByName(groupName).orElseThrow(() -> new NotFoundException(GroupConstants.GROUP_NOT_FOUND));

        if (!groupObj.getType().equals("local")) {
            throw new BadRequestException(GroupConstants.REMOTE_GROUP);
        }

        GroupUpdateResponse response = new GroupUpdateResponse();
        response.setAdded(new ArrayList<>());
        response.setRemoved(new ArrayList<>());
        response.setRejected(new ArrayList<>());
        response.setGroup(groupName);

        groupUpdateRequest.getUsers().forEach(user -> {
            if (groupUpdateRequest.getAction() == Action.ADD) {
                if (!userGroupsPersistence.addUserToGroup(groupName, user)) {
                    response.getRejected().add(user);
                    return;
                }
                response.getAdded().add(user);
            } else { //REMOVE
                if (!userGroupsPersistence.removeUserFromGroup(groupName, user)) {
                    response.getRejected().add(user);
                    return;
                }
                response.getRemoved().add(user);
            }
        });
        return response;
    }

    protected void handleSingleResponse(BaseResponse<GroupsResponse> res) {
        if (res.getRejected() != null && !res.getRejected().isEmpty()) {
            List<Rejection> rejected = res.getRejected();
            int code = rejected.get(0).getCode();
            switch(code) {
                case 304:
                    throw new NotModifiedException(res);
                case 400:
                    throw new BadRequestException(res);
                case 401:
                    throw new UnauthorizedException(res);
                case 403:
                    throw new ForbiddenException(res);
                case 404:
                    throw new NotFoundException(res);
                case 409:
                    throw new ConflictException(res);
                case 410:
                    throw new DeletedException(res);
                case 500:
                    throw new InternalErrorException(res);
                default:
                    break;
            }
        }
    }
}
