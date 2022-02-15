package org.openmbee.mms.permissions.config;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.openmbee.mms.core.config.AuthorizationConstants;
import org.openmbee.mms.core.objects.PermissionUpdateRequest;
import org.openmbee.mms.core.services.PermissionService;
import org.openmbee.mms.data.domains.global.*;
import org.openmbee.mms.rdb.repositories.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

import static org.openmbee.mms.core.config.Constants.RPmap;
import static org.openmbee.mms.core.config.Constants.aPriv;

@Component
@Transactional
public class PermissionInit implements ApplicationListener<ApplicationReadyEvent> {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private PrivilegeRepository privRepo;

    private RoleRepository roleRepo;

    private GroupRepository groupRepo;

    private UserRepository userRepo;

    private GroupGroupPermRepository groupGroupPermRepo;

    private PermissionService permissionService;

    @Autowired
    public void setPrivRepo(PrivilegeRepository privRepo) {
        this.privRepo = privRepo;
    }

    @Autowired
    public void setRoleRepo(RoleRepository roleRepo) {
        this.roleRepo = roleRepo;
    }

    @Autowired
    public void setGroupRepo(GroupRepository groupRepo) {
        this.groupRepo = groupRepo;
    }

    @Autowired
    public void setUserRepo(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    @Autowired
    public void setGroupGroupPermRepo(GroupGroupPermRepository groupGroupPermRepo) {
        this.groupGroupPermRepo = groupGroupPermRepo;
    }

    @Override
    public void onApplicationEvent(final ApplicationReadyEvent event) {
        logger.warn("I RAN!");
        for (String role : RPmap.keySet()) {
            Optional<Role> roleIn = roleRepo.findByName(role);
            if (!(roleIn.isPresent())) {
                Role missingRole = new Role();
                missingRole.setName(role);
                roleRepo.saveAndFlush(missingRole);
            }
        }

        for (String privs : aPriv) {
            Optional<Privilege> privIn = privRepo.findByName(privs);
            if (!(privIn.isPresent())) {
                Privilege missingPriv = new Privilege();
                missingPriv.setName(privs);
                privRepo.saveAndFlush(missingPriv);
            }
        }

        List<Role> roleList = roleRepo.findAll();
        List<Privilege> privList = privRepo.findAll();

        for (Role role : roleList) {
            Set<Privilege> pSet = new HashSet<Privilege>();
            for (Privilege priv : privList) {
                if ((RPmap.get(role.getName())).contains(priv.getName())) {
                    pSet.add(priv);
                }
            }
            role.setPrivileges(pSet);
            roleRepo.saveAndFlush(role);
        }

        Optional<Group> evGroupIn = groupRepo.findByName("everyone");
        Group evGroup;
        if (evGroupIn.isEmpty()) {
            Optional<Role> evRole = roleRepo.findByName(AuthorizationConstants.READER);
            evGroup = new Group();
            evGroup.setName("everyone");
            evGroup.setType(Group.VALID_GROUP_TYPES.LOCAL);
            evGroup.getUsers().addAll(userRepo.findAll());
            groupRepo.saveAndFlush(evGroup);
            if (evRole.isPresent()) {
                GroupGroupPerm evGroupPerm = new GroupGroupPerm(evGroup, evGroup, evRole.get());
                groupGroupPermRepo.save(evGroupPerm);
            }
        }else {
            evGroup = evGroupIn.get();
            //Validate Everyone group is properly populated
            Set<User> allUsers = new HashSet<>(userRepo.findAll());
            evGroup.getUsers().removeIf(user -> !allUsers.contains(user));
            allUsers.removeAll(evGroup.getUsers());
            if (allUsers.size() > 0) {
                evGroup.getUsers().addAll(allUsers);
            }
            groupRepo.saveAndFlush(evGroup);
        }

    }
}