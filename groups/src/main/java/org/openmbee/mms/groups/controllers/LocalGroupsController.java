package org.openmbee.mms.groups.controllers;

import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.openmbee.mms.core.config.AuthorizationConstants;
import org.openmbee.mms.core.exceptions.BadRequestException;
import org.openmbee.mms.core.exceptions.ConflictException;
import org.openmbee.mms.core.exceptions.NotFoundException;
import org.openmbee.mms.data.domains.global.Group;
import org.openmbee.mms.data.domains.global.User;
import org.openmbee.mms.groups.constants.GroupConstants;
import org.openmbee.mms.groups.objects.*;
import org.openmbee.mms.groups.services.GroupValidationService;
import org.openmbee.mms.rdb.repositories.GroupRepository;
import org.openmbee.mms.rdb.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/groups")
@Tag(name = "Groups")
@Transactional
public class LocalGroupsController {

    private GroupRepository groupRepository;
    private GroupValidationService groupValidationService;
    private UserRepository userRepository;

    @Autowired
    public void setGroupRepository(GroupRepository groupRepository) {
        this.groupRepository = groupRepository;
    }

    @Autowired
    public void setGroupValidationService(GroupValidationService groupValidationService) {
        this.groupValidationService = groupValidationService;
    }

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PutMapping("/{group}")
    @PreAuthorize(AuthorizationConstants.IS_MMSADMIN)
    @ResponseBody
    public void createLocalGroup(@PathVariable String group) {
        Group groupObj = groupRepository.findByName(group).orElse(null);
        if (groupObj != null) {
            throw new ConflictException(GroupConstants.GROUP_ALREADY_EXISTS);
        }

        if (!groupValidationService.isValidGroupName(group)) {
            throw new BadRequestException(GroupConstants.INVALID_GROUP_NAME);
        }

        groupObj = new Group();
        groupObj.setName(group);
        groupRepository.saveAndFlush(groupObj);
    }

    @GetMapping
    @PreAuthorize(AuthorizationConstants.IS_MMSADMIN)
    public GroupsResponse getAllGroups() {
        List<Group> groups = groupRepository.findAll(Sort.by(GroupConstants.NAME));
        GroupsResponse response = new GroupsResponse();
        response.setGroups(groups.stream().map(Group::getName).collect(Collectors.toList()));
        return response;
    }

    @GetMapping("/{group}")
    @PreAuthorize(AuthorizationConstants.IS_MMSADMIN)
    @Transactional
    public GroupResponse getGroup(@PathVariable String group) {
        if(groupValidationService.isRestrictedGroup(group)) {
            throw new BadRequestException(GroupConstants.RESTRICTED_GROUP);
        }
        return new GroupResponse(groupRepository.findByName(group).orElseThrow(() -> new NotFoundException(GroupConstants.GROUP_NOT_FOUND)));
    }

    @DeleteMapping("/{group}")
    @PreAuthorize(AuthorizationConstants.IS_MMSADMIN)
    @ResponseBody
    @Transactional
    public void deleteLocalGroup(@PathVariable String group) {
        Group groupObj = groupRepository.findByName(group).orElseThrow(() -> new NotFoundException(GroupConstants.GROUP_NOT_FOUND));
        if (groupValidationService.canDeleteGroup(groupObj)) {
            this.groupRepository.delete(groupObj);
        } else {
            throw new BadRequestException(GroupConstants.GROUP_NOT_EMPTY);
        }
    }

    @PostMapping("/{group}/users")
    @PreAuthorize(AuthorizationConstants.IS_MMSADMIN)
    @Transactional
    public GroupUpdateResponse updateGroupUsers(@PathVariable String group,
            @RequestBody GroupUpdateRequest groupUpdateRequest) {

        if (groupUpdateRequest.getAction() == null) {
            throw new BadRequestException(GroupConstants.INVALID_ACTION);
        }

        if (groupUpdateRequest.getUsers() == null ||
            groupUpdateRequest.getUsers().isEmpty()) {
            throw new BadRequestException(GroupConstants.NO_USERS_PROVIDED);
        }

        if (groupValidationService.isRestrictedGroup(group)) {
            throw new BadRequestException(GroupConstants.RESTRICTED_GROUP);
        }

        Group groupObj = groupRepository.findByName(group).orElseThrow(() -> new NotFoundException(GroupConstants.GROUP_NOT_FOUND));
        GroupUpdateResponse response = new GroupUpdateResponse();
        response.setAdded(new ArrayList<>());
        response.setRemoved(new ArrayList<>());
        response.setRejected(new ArrayList<>());
        response.setGroup(group);

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
}
