package org.openmbee.sdvc.permissions.delegation;

import org.openmbee.sdvc.core.delegation.PermissionsDelegate;
import org.openmbee.sdvc.core.objects.PermissionUpdateRequest;
import org.openmbee.sdvc.data.domains.global.Group;
import org.openmbee.sdvc.data.domains.global.Role;
import org.openmbee.sdvc.rdb.repositories.GroupRepository;
import org.openmbee.sdvc.rdb.repositories.RoleRepository;
import org.springframework.data.util.Pair;

import java.util.Optional;

public abstract class AbstractDefaultPermissionsDelegate implements PermissionsDelegate {

    abstract protected GroupRepository getGroupRepo();
    abstract protected RoleRepository getRoleRepo();

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
