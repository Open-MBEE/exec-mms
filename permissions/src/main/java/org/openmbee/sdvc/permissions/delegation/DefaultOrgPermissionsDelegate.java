package org.openmbee.sdvc.permissions.delegation;

import org.openmbee.sdvc.core.builders.PermissionUpdateResponseBuilder;
import org.openmbee.sdvc.core.config.AuthorizationConstants;
import org.openmbee.sdvc.core.objects.PermissionResponse;
import org.openmbee.sdvc.core.objects.PermissionUpdateRequest;
import org.openmbee.sdvc.core.objects.PermissionUpdateResponse;
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
    public PermissionUpdateResponse updateUserPermissions(PermissionUpdateRequest req) {
        PermissionUpdateResponseBuilder responseBuilder = new PermissionUpdateResponseBuilder();

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
                            responseBuilder.addPermissionUpdate(req.getAction(), user.get().getUsername(), role.get().getName(),
                                organization.getOrganizationId(), organization.getOrganizationName(), false);
                        }
                    } else {
                        orgUserPermRepo.save(new OrgUserPerm(organization, user.get(), role.get()));
                        responseBuilder.addPermissionUpdate(req.getAction(), user.get().getUsername(), role.get().getName(),
                            organization.getOrganizationId(), organization.getOrganizationName(), false);
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
                    responseBuilder.addPermissionUpdate(req.getAction(), user.get().getUsername(), role.get().getName(),
                        organization.getOrganizationId(), organization.getOrganizationName(), false);
                }
                break;
            case REMOVE:
                Set<String> users = new HashSet<>();
                req.getPermissions().forEach(p -> {
                    users.add(p.getName());
                    responseBuilder.addPermissionUpdate(req.getAction(), p.getName(), null,
                        organization.getOrganizationId(), organization.getOrganizationName(), false);
                });
                orgUserPermRepo.deleteByOrganizationAndUser_UsernameIn(organization, users);
                break;
        }
        return responseBuilder.getPermissionUpdateReponse();
    }

    @Override
    public PermissionUpdateResponse updateGroupPermissions(PermissionUpdateRequest req) {
        PermissionUpdateResponseBuilder responseBuilder = new PermissionUpdateResponseBuilder();
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
                            responseBuilder.addPermissionUpdate(req.getAction(), pair.getFirst().getName(), pair.getSecond().getName(),
                                organization.getOrganizationId(), organization.getOrganizationName(), false);
                        }
                    } else {
                        orgGroupPermRepo.save(new OrgGroupPerm(organization, pair.getFirst(), pair.getSecond()));
                        responseBuilder.addPermissionUpdate(req.getAction(), pair.getFirst().getName(), pair.getSecond().getName(),
                            organization.getOrganizationId(), organization.getOrganizationName(), false);
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
                    responseBuilder.addPermissionUpdate(req.getAction(), pair.getFirst().getName(), pair.getSecond().getName(),
                        organization.getOrganizationId(), organization.getOrganizationName(), false);
                }
                break;
            case REMOVE:
                Set<String> groups = new HashSet<>();
                req.getPermissions().forEach(p -> {
                    groups.add(p.getName());
                    responseBuilder.addPermissionUpdate(req.getAction(), p.getName(), null,
                        organization.getOrganizationId(), organization.getOrganizationName(), false);
                });
                orgGroupPermRepo.deleteByOrganizationAndGroup_NameIn(organization, groups);
                break;
        }
        return responseBuilder.getPermissionUpdateReponse();
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
    public PermissionUpdateResponse recalculateInheritedPerms() {
        //Do nothing, can't inherit permissions
        return new PermissionUpdateResponseBuilder().getPermissionUpdateReponse();
    }

}
