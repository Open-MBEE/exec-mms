package org.openmbee.mms.groups.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.openmbee.mms.core.config.AuthorizationConstants;
import org.openmbee.mms.core.config.Privileges;
import org.openmbee.mms.core.exceptions.*;
import org.openmbee.mms.core.objects.*;
import org.openmbee.mms.core.security.MethodSecurityService;
import org.openmbee.mms.core.services.PermissionService;
import org.openmbee.mms.data.domains.global.Group;
import org.openmbee.mms.data.domains.global.User;
import org.openmbee.mms.groups.constants.GroupConstants;
import org.openmbee.mms.groups.objects.*;
import org.openmbee.mms.groups.services.GroupValidationService;
import org.openmbee.mms.json.GroupJson;
import org.openmbee.mms.rdb.repositories.GroupRepository;
import org.openmbee.mms.rdb.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/groups")
@Tag(name = "Groups")
@Transactional
public class GroupsController {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    protected ObjectMapper om;

    private GroupRepository groupRepository;
    private GroupValidationService groupValidationService;
    private UserRepository userRepository;

    protected PermissionService permissionService;

    protected MethodSecurityService mss;

    public Map<String, Object> convertToMap(Object obj) {
        return om.convertValue(obj, new TypeReference<Map<String, Object>>() {});
    }

    @Autowired
    public void setGroupRepository(GroupRepository groupRepository) {
        this.groupRepository = groupRepository;
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

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
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

            Group g = groupRepository.findByName(group.getName())
                .orElse(new Group());
            boolean newGroup = true;
            if (g.getId() != null) {
                if (!mss.hasGroupPrivilege(auth, g.getName(), Privileges.GROUP_EDIT.name(), false)) {
                    response.addRejection(new Rejection(group, 403, "No permission to update group"));
                    continue;
                }
                newGroup = false;
            }
            g.setName(group.getName());
            g.setType(group.getType());
            logger.info("Saving group: {}", g.getName());
            Group saved = groupRepository.save(g);
            if (newGroup) {
                permissionService.initGroupPerms(group.getName(), auth.getName());
            }
            group.merge(convertToMap(saved));
            response.getGroups().add(group);
        }
        if (groupPost.getGroups().size() == 1) {
            handleSingleResponse(response);
        }
        return response;
    }

    @GetMapping
    @Transactional
    public GroupsResponse getAllGroups( Authentication auth) {

        GroupsResponse response = new GroupsResponse();
        List<Group> allGroups = groupRepository.findAll();
        for (Group group : allGroups) {
            if (mss.hasGroupPrivilege(auth, group.getName(), Privileges.GROUP_READ.name(), true)) {
                GroupJson groupJson = new GroupJson();
                groupJson.merge(convertToMap(group));
                response.getGroups().add(groupJson);
            }
        }
        return response;
    }

    @GetMapping(value = "/{groupName}")
    @Transactional
    @PreAuthorize("@mss.hasGroupPrivilege(authentication, #groupName, 'GROUP_READ', true)")
    public GroupsResponse getGroup(
        @PathVariable String groupName) {

        GroupsResponse response = new GroupsResponse();
        Optional<Group> groupOption = groupRepository.findByName(groupName);
        if (groupOption.isEmpty()) {
            throw new NotFoundException(response.addMessage("Group not found."));
        }
        GroupJson groupJson = new GroupJson();
        groupJson.merge(convertToMap(groupOption.get()));
        response.getGroups().add(groupJson);
        return response;
    }

    @DeleteMapping("/{groupName}")
    @PreAuthorize("@mss.hasGroupPrivilege(authentication, #groupName, 'GROUP_DELETE', false)")
    @ResponseBody
    @Transactional
    public void deleteLocalGroup(@PathVariable String groupName) {
        Group groupObj = groupRepository.findByName(groupName).orElseThrow(() -> new NotFoundException(GroupConstants.GROUP_NOT_FOUND));
        if (groupValidationService.isRestrictedGroup(groupObj.getName())) {
            throw new BadRequestException(GroupConstants.NO_DELETE_RESTRICTED);
        }
        if (groupValidationService.canDeleteGroup(groupObj)) {
            this.groupRepository.delete(groupObj);
        } else {
            throw new BadRequestException(GroupConstants.GROUP_NOT_EMPTY);
        }
    }

    @GetMapping("/{groupName}/users")
    @PreAuthorize("@mss.hasGroupPrivilege(authentication, #groupName, 'GROUP_READ', true)")
    @Transactional
    public GroupUsersResponse getAllGroupUsers( @PathVariable String groupName) {

        GroupUsersResponse response = new GroupUsersResponse();
        Set<String> usernames = new HashSet<>();
        Group groupObj = groupRepository.findByName(groupName).orElseThrow(() -> new NotFoundException(GroupConstants.GROUP_NOT_FOUND));
        if (groupObj.getType() == Group.VALID_GROUP_TYPES.REMOTE) {
            usernames.addAll(userRepository.findAll().stream().filter(user -> (user.getGroups().contains(groupObj))).map(User::getUsername).collect(Collectors.toSet()));
        }else {
            usernames.addAll(groupObj.getUsers().stream().map(User::getUsername).collect(Collectors.toSet()));
        }
        response.getUsers().addAll(usernames);
        return response;
    }

    @PostMapping("/{groupName}/users")
    @PreAuthorize("@mss.hasGroupPrivilege(authentication, #groupName, 'GROUP_EDIT', true)")
    @Transactional
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

        Group groupObj = groupRepository.findByName(groupName).orElseThrow(() -> new NotFoundException(GroupConstants.GROUP_NOT_FOUND));

        if (groupObj.getType() == Group.VALID_GROUP_TYPES.REMOTE) {
            throw new BadRequestException(GroupConstants.REMOTE_GROUP);
        }

        GroupUpdateResponse response = new GroupUpdateResponse();
        response.setAdded(new ArrayList<>());
        response.setRemoved(new ArrayList<>());
        response.setRejected(new ArrayList<>());
        response.setGroup(groupName);

        groupUpdateRequest.getUsers().forEach(newUser -> {
            User user = userRepository.findByUsername(newUser).orElse(null);
            if (user != null) {

                if (groupUpdateRequest.getAction() == Action.ADD) {
                    if(groupObj.getUsers().contains(user)){
                        response.getRejected().add(newUser);
                        return;
                    }
                    user.getGroups().add(groupObj);
                    response.getAdded().add(user.getUsername());
                } else { //REMOVE
                    if(!groupObj.getUsers().contains(user)){
                        response.getRejected().add(newUser);
                        return;
                    }
                    user.getGroups().remove(groupObj);
                    response.getRemoved().add(user.getUsername());
                }
                userRepository.save(user);
            } else {
                //Reject users that don't exist
                response.getRejected().add(newUser);
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
