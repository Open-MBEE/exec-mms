package org.openmbee.sdvc.permissions.delegation;

import org.openmbee.sdvc.core.delegation.PermissionsDelegate;
import org.openmbee.sdvc.core.objects.PermissionUpdateRequest;
import org.openmbee.sdvc.data.domains.global.Group;
import org.openmbee.sdvc.data.domains.global.Role;
import org.openmbee.sdvc.rdb.repositories.GroupRepository;
import org.openmbee.sdvc.rdb.repositories.PrivilegeRepository;
import org.openmbee.sdvc.rdb.repositories.RoleRepository;
import org.openmbee.sdvc.rdb.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;

import java.util.Optional;

public abstract class AbstractDefaultPermissionsDelegate implements PermissionsDelegate {

    private UserRepository userRepo;
    private GroupRepository groupRepo;
    private RoleRepository roleRepo;
    private PrivilegeRepository privRepo;

    @Autowired
    public void setUserRepo(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    @Autowired
    public void setGroupRepo(GroupRepository groupRepo) {
        this.groupRepo = groupRepo;
    }

    @Autowired
    public void setRoleRepo(RoleRepository roleRepo) {
        this.roleRepo = roleRepo;
    }

    @Autowired
    public void setPrivRepo(PrivilegeRepository privRepo) {
        this.privRepo = privRepo;
    }

    public UserRepository getUserRepo() {
        return userRepo;
    }

    public GroupRepository getGroupRepo() {
        return groupRepo;
    }

    public RoleRepository getRoleRepo() {
        return roleRepo;
    }

    public PrivilegeRepository getPrivRepo() {
        return privRepo;
    }

    protected Pair<Group, Role> getGroupAndRole(PermissionUpdateRequest.Permission p) {
        Optional<Group> group = getGroupRepo().findByName(p.getName());
        Optional<Role> role = getRoleRepo().findByName(p.getRole());
        if (!role.isPresent()) {
            return Pair.of(group.orElse(null), null);
        }
        if (!group.isPresent()) {
            group = Optional.of(new Group(p.getName()));
            getGroupRepo().save(group.get());
        }
        return Pair.of(group.get(), role.get());
    }

}
