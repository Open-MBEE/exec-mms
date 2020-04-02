package org.openmbee.sdvc.permissions.delegation;

import org.openmbee.sdvc.core.config.AuthorizationConstants;
import org.openmbee.sdvc.core.objects.PermissionResponse;
import org.openmbee.sdvc.core.objects.PermissionUpdateRequest;
import org.openmbee.sdvc.data.domains.global.*;
import org.openmbee.sdvc.permissions.exceptions.PermissionException;
import org.openmbee.sdvc.rdb.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpStatus;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class DefaultOrgPermissionsDelegate extends AbstractDefaultPermissionsDelegate {

    private OrganizationRepository orgRepo;
    private OrgUserPermRepository orgUserPermRepo;
    private OrgGroupPermRepository orgGroupPermRepo;

    private Organization organization;

    public DefaultOrgPermissionsDelegate(Organization organization) {
        this.organization = organization;
    }

    @Autowired
    public void setOrgRepo(OrganizationRepository orgRepo) {
        this.orgRepo = orgRepo;
    }

    @Autowired
    public void setOrgUserPermRepo(OrgUserPermRepository orgUserPermRepo) {
        this.orgUserPermRepo = orgUserPermRepo;
    }

    @Autowired
    public void setOrgGroupPermRepo(OrgGroupPermRepository orgGroupPermRepo) {
        this.orgGroupPermRepo = orgGroupPermRepo;
    }

    @Override
    public boolean hasPermission(String user, Set<String> groups, String privilege) {

        Optional<Privilege> priv = getPrivRepo().findByName(privilege);
        if (!priv.isPresent()) {
            throw new PermissionException(HttpStatus.BAD_REQUEST, "No such privilege");
        }

        Set<Role> roles = priv.get().getRoles();
        if (orgUserPermRepo.existsByOrganizationAndUser_UsernameAndRoleIn(organization, user, roles)) {
            return true;
        }
        if (!groups.isEmpty() && orgGroupPermRepo.existsByOrganizationAndGroup_NameInAndRoleIn(organization, groups, roles)) {
            return true;
        }
        return false;
    }

    @Override
    public void initializePermissions(String creator) {
        initializePermissions(creator, false);
    }

    @Override
    public void initializePermissions(String creator, boolean inherit) {
        if(inherit) {
            throw new IllegalArgumentException("Cannot inherit permissions for an Org");
        }

        Optional<User> user = getUserRepo().findByUsername(creator);
        Optional<Role> role = getRoleRepo().findByName(AuthorizationConstants.ADMIN);

        if (!user.isPresent()) {
            throw new PermissionException(HttpStatus.NOT_FOUND, "User not found");
        } else if (!role.isPresent()) {
            throw new PermissionException(HttpStatus.NOT_FOUND, "Role not found");
        }

        OrgUserPerm perm = new OrgUserPerm(organization, user.get(), role.get());
        orgUserPermRepo.save(perm);
    }

    @Override
    public boolean setInherit(boolean isInherit) {
        if(isInherit) {
            throw new IllegalArgumentException("Cannot inherit permissions for an Org");
        }
        return false;
    }

    @Override
    public void setPublic(boolean isPublic) {
        organization.setPublic(isPublic);
        orgRepo.save(organization);
    }

    @Override
    public void updateUserPermissions(PermissionUpdateRequest req) {

        switch(req.getAction()) {
            case MODIFY:
                for (PermissionUpdateRequest.Permission p: req.getPermissions()) {
                    Optional<User> user = getUserRepo().findByUsername(p.getName());
                    Optional<Role> role = getRoleRepo().findByName(p.getRole());
                    if (!user.isPresent() || !role.isPresent()) {
                        //throw exception or skip
                        continue;
                    }
                    Optional<OrgUserPerm> exist = orgUserPermRepo.findByOrganizationAndUser(organization, user.get());
                    if (exist.isPresent()) {
                        OrgUserPerm e = exist.get();
                        if (!role.get().equals(e.getRole())) {
                            e.setRole(role.get());
                            orgUserPermRepo.save(e);
                        }
                    } else {
                        orgUserPermRepo.save(new OrgUserPerm(organization, user.get(), role.get()));
                    }
                }
                break;
            case REPLACE:
                orgUserPermRepo.deleteByOrganization(organization);
                for (PermissionUpdateRequest.Permission p: req.getPermissions()) {
                    Optional<User> user = getUserRepo().findByUsername(p.getName());
                    Optional<Role> role = getRoleRepo().findByName(p.getRole());
                    if (!user.isPresent() || !role.isPresent()) {
                        //throw exception or skip
                        continue;
                    }
                    orgUserPermRepo.save(new OrgUserPerm(organization, user.get(), role.get()));
                }
                break;
            case REMOVE:
                Set<String> users = new HashSet<>();
                req.getPermissions().forEach(p -> users.add(p.getName()));
                orgUserPermRepo.deleteByOrganizationAndUser_UsernameIn(organization, users);
                break;
        }
    }

    @Override
    public void updateGroupPermissions(PermissionUpdateRequest req) {
        switch(req.getAction()) {
            case MODIFY:
                for (PermissionUpdateRequest.Permission p: req.getPermissions()) {
                    Pair<Group, Role> pair = getGroupAndRole(p);
                    if (pair.getFirst() == null || pair.getSecond() == null) {
                        continue;
                    }
                    Optional<OrgGroupPerm> exist = orgGroupPermRepo.findByOrganizationAndGroup(organization, pair.getFirst());
                    if (exist.isPresent()) {
                        OrgGroupPerm e = exist.get();
                        if (!pair.getSecond().equals(e.getRole())) {
                            e.setRole(pair.getSecond());
                            orgGroupPermRepo.save(e);
                        }
                    } else {
                        orgGroupPermRepo.save(new OrgGroupPerm(organization, pair.getFirst(), pair.getSecond()));
                    }
                }
                break;
            case REPLACE:
                orgGroupPermRepo.deleteByOrganization(organization);
                for (PermissionUpdateRequest.Permission p: req.getPermissions()) {
                    Pair<Group, Role> pair = getGroupAndRole(p);
                    if (pair.getFirst() == null || pair.getSecond() == null) {
                        continue;
                    }
                    orgGroupPermRepo.save(new OrgGroupPerm(organization, pair.getFirst(), pair.getSecond()));
                }
                break;
            case REMOVE:
                Set<String> groups = new HashSet<>();
                req.getPermissions().forEach(p -> groups.add(p.getName()));
                orgGroupPermRepo.deleteByOrganizationAndGroup_NameIn(organization, groups);
                break;
        }
    }

    @Override
    public PermissionResponse getUserRoles() {
        PermissionResponse res = PermissionResponse.getDefaultResponse();
        for (OrgUserPerm perm: orgUserPermRepo.findAllByOrganization_OrganizationId(organization.getOrganizationId())) {
            res.getPermissions().add(new PermissionResponse.Permission(
                perm.getUser().getUsername(),
                perm.getRole().getName(),
                false
            ));
        }
        return res;
    }

    @Override
    public PermissionResponse getGroupRoles() {
        PermissionResponse res = PermissionResponse.getDefaultResponse();
        for (OrgGroupPerm perm: orgGroupPermRepo.findAllByOrganization_OrganizationId(organization.getOrganizationId())) {
            res.getPermissions().add(new PermissionResponse.Permission(
                perm.getGroup().getName(),
                perm.getRole().getName(),
                false
            ));
        }
        return res;
    }

    @Override
    public void recalculateInheritedPerms() {
        //Do nothing, can't inherit permissions
    }

}
