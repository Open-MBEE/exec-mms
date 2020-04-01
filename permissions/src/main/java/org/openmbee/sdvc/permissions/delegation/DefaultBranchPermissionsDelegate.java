package org.openmbee.sdvc.permissions.delegation;

import org.openmbee.sdvc.core.builders.PermissionUpdateResponseBuilder;
import org.openmbee.sdvc.core.builders.PermissionUpdatesResponseBuilder;
import org.openmbee.sdvc.core.config.AuthorizationConstants;
import org.openmbee.sdvc.core.objects.PermissionResponse;
import org.openmbee.sdvc.core.objects.PermissionUpdateRequest;
import org.openmbee.sdvc.core.objects.PermissionUpdateResponse;
import org.openmbee.sdvc.core.objects.PermissionUpdatesResponse;
import org.openmbee.sdvc.data.domains.global.*;
import org.openmbee.sdvc.permissions.exceptions.PermissionException;
import org.openmbee.sdvc.rdb.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpStatus;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class DefaultBranchPermissionsDelegate extends AbstractDefaultPermissionsDelegate {

    private BranchRepository branchRepo;
    private BranchUserPermRepository branchUserPermRepo;
    private BranchGroupPermRepository branchGroupPermRepo;
    private ProjectGroupPermRepository projectGroupPermRepo;
    private ProjectUserPermRepository projectUserPermRepo;
    private Branch branch;

    public DefaultBranchPermissionsDelegate(Branch branch) {
        this.branch = branch;
    }

    @Autowired
    public void setBranchRepo(BranchRepository branchRepo) {
        this.branchRepo = branchRepo;
    }

    @Autowired
    public void setBranchUserPermRepo(BranchUserPermRepository branchUserPermRepo) {
        this.branchUserPermRepo = branchUserPermRepo;
    }

    @Autowired
    public void setBranchGroupPermRepo(BranchGroupPermRepository branchGroupPermRepo) {
        this.branchGroupPermRepo = branchGroupPermRepo;
    }

    @Autowired
    public void setProjectGroupPermRepo(ProjectGroupPermRepository projectGroupPermRepo) {
        this.projectGroupPermRepo = projectGroupPermRepo;
    }

    @Autowired
    public void setProjectUserPermRepo(ProjectUserPermRepository projectUserPermRepo) {
        this.projectUserPermRepo = projectUserPermRepo;
    }

    @Override
    public boolean hasPermission(String user, Set<String> groups, String privilege) {

        Optional<Privilege> priv = getPrivRepo().findByName(privilege);
        if (!priv.isPresent()) {
            throw new PermissionException(HttpStatus.BAD_REQUEST, "No such privilege");
        }

        Set<Role> roles = priv.get().getRoles();
        if (branchUserPermRepo.existsByBranchAndUser_UsernameAndRoleIn(branch, user, roles)) {
            return true;
        }
        if (!groups.isEmpty() && branchGroupPermRepo.existsByBranchAndGroup_NameInAndRoleIn(branch, groups, roles)) {
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

        Optional<User> user = getUserRepo().findByUsername(creator);
        Optional<Role> role = getRoleRepo().findByName("ADMIN");

        if (!user.isPresent()) {
            throw new PermissionException(HttpStatus.NOT_FOUND, "User not found");
        } else if (!role.isPresent()) {
            throw new PermissionException(HttpStatus.NOT_FOUND, "Role not found");
        }

        branch.setInherit(inherit);
        branchRepo.save(branch);

        BranchUserPerm perm = new BranchUserPerm(branch, user.get(), role.get(), false);
        branchUserPermRepo.save(perm);
    }

    @Override
    public boolean setInherit(boolean isInherit) {
        if (branch.isInherit() != isInherit) {
            branch.setInherit(isInherit);
            branchRepo.save(branch);
            return true;
        }
        return false;
    }

    @Override
    public void setPublic(boolean isPublic) {
        //Not currently supported
        //TODO should this be supported?
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
                    Optional<BranchUserPerm> exist = branchUserPermRepo.findByBranchAndUserAndInheritedIsFalse(branch, user.get());
                    BranchUserPerm perm;
                    if (exist.isPresent()) {
                        perm = exist.get();
                        if (!role.get().equals(perm.getRole())) {
                            responseBuilder.insertPermissionUpdate(PermissionUpdateResponse.Action.REMOVE, perm);
                            perm.setRole(role.get());
                            branchUserPermRepo.save(perm);
                        } else {
                            continue;
                        }
                    } else {
                        perm = new BranchUserPerm(branch, user.get(), role.get(), false);
                        branchUserPermRepo.save(perm);
                    }
                    responseBuilder.insertPermissionUpdate(PermissionUpdateResponse.Action.ADD, perm);
                }
                break;
            case REPLACE:
                responseBuilder.insertPermissionUpdates_BranchUserPerm(PermissionUpdateResponse.Action.REMOVE,
                    branchUserPermRepo.findAllByBranchAndInherited(branch, false));
                branchUserPermRepo.deleteByBranchAndInherited(branch, false);
                for (PermissionUpdateRequest.Permission p: req.getPermissions()) {
                    Optional<User> user = getUserRepo().findByUsername(p.getName());
                    Optional<Role> role = getRoleRepo().findByName(p.getRole());
                    if (!user.isPresent() || !role.isPresent()) {
                        //throw exception or skip
                        continue;
                    }
                    BranchUserPerm perm = new BranchUserPerm(branch, user.get(), role.get(), false);
                    branchUserPermRepo.save(perm);
                    responseBuilder.insertPermissionUpdate(PermissionUpdateResponse.Action.ADD, perm);
                }
                break;
            case REMOVE:
                Set<String> users = new HashSet<>();
                req.getPermissions().forEach(p -> {
                    Optional<User> user = getUserRepo().findByUsername(p.getName());
                    if(! user.isPresent()) {
                        //throw or skip;
                        return;
                    }

                    users.add(p.getName());
                    responseBuilder.insertPermissionUpdate(PermissionUpdateResponse.Action.REMOVE,
                        branchUserPermRepo.findByBranchAndUserAndInheritedIsFalse(branch, user.get()).orElse(null));
                });
                branchUserPermRepo.deleteByBranchAndUser_UsernameInAndInheritedIsFalse(branch, users);
                break;
        }
        return responseBuilder.getPermissionUpdateResponse();
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
                    Optional<BranchGroupPerm> exist = branchGroupPermRepo.findByBranchAndGroupAndInheritedIsFalse(branch, pair.getFirst());
                    BranchGroupPerm perm;
                    if (exist.isPresent()) {
                        perm = exist.get();
                        if (!pair.getSecond().equals(perm.getRole())) {
                            responseBuilder.insertPermissionUpdate(PermissionUpdateResponse.Action.REMOVE, perm);
                            perm.setRole(pair.getSecond());
                            branchGroupPermRepo.save(perm);
                        } else {
                            continue;
                        }
                    } else {
                        perm = new BranchGroupPerm(branch, pair.getFirst(), pair.getSecond(), false);
                        branchGroupPermRepo.save(perm);
                    }
                    responseBuilder.insertPermissionUpdate(PermissionUpdateResponse.Action.ADD, perm);
                }
                break;
            case REPLACE:
                responseBuilder.insertPermissionUpdates_BranchGroupPerm(PermissionUpdateResponse.Action.REMOVE,
                    branchGroupPermRepo.findAllByBranchAndInherited(branch, false));
                branchGroupPermRepo.deleteByBranchAndInherited(branch, false);
                for (PermissionUpdateRequest.Permission p: req.getPermissions()) {
                    Pair<Group, Role> pair = getGroupAndRole(p);
                    if (pair.getFirst() == null || pair.getSecond() == null) {
                        continue;
                    }
                    BranchGroupPerm perm = new BranchGroupPerm(branch, pair.getFirst(), pair.getSecond(), false);
                    branchGroupPermRepo.save(perm);
                    responseBuilder.insertPermissionUpdate(PermissionUpdateResponse.Action.ADD, perm);
                }
                break;
            case REMOVE:
                Set<String> groups = new HashSet<>();
                req.getPermissions().forEach(p -> {
                    Optional<Group> group = getGroupRepo().findByName(p.getName());
                    if(! group.isPresent()) {
                        //throw or skip
                        return;
                    }

                    groups.add(p.getName());
                    responseBuilder.insertPermissionUpdate(PermissionUpdateResponse.Action.REMOVE,
                        branchGroupPermRepo.findByBranchAndGroupAndInheritedIsFalse(branch, group.get()).orElse(null));
                });
                branchGroupPermRepo.deleteByBranchAndGroup_NameInAndInheritedIsFalse(branch, groups);
                break;
        }
        return responseBuilder.getPermissionUpdateResponse();
    }

    @Override
    public PermissionResponse getUserRoles() {
        PermissionResponse res = PermissionResponse.getDefaultResponse();
        for (BranchUserPerm perm: branchUserPermRepo.findAllByBranch(branch)) {
            res.getPermissions().add(new PermissionResponse.Permission(
                perm.getUser().getUsername(),
                perm.getRole().getName(),
                perm.isInherited()
            ));
        }
        return res;
    }

    @Override
    public PermissionResponse getGroupRoles() {
        PermissionResponse res = PermissionResponse.getDefaultResponse();
        for (BranchGroupPerm perm: branchGroupPermRepo.findAllByBranch(branch)) {
            res.getPermissions().add(new PermissionResponse.Permission(
                perm.getGroup().getName(),
                perm.getRole().getName(),
                perm.isInherited()
            ));
        }
        return res;
    }

    @Override
    public PermissionUpdatesResponse recalculateInheritedPerms() {
        PermissionUpdatesResponseBuilder responseBuilder = new PermissionUpdatesResponseBuilder();

        Collection<BranchUserPerm> branchUserPerms = branchUserPermRepo.findAllByBranchAndInherited(branch, true);
        branchUserPermRepo.deleteAll(branchUserPerms);
        responseBuilder.getUsers().insertPermissionUpdates_BranchUserPerm(PermissionUpdateResponse.Action.REMOVE, branchUserPerms);

        Collection<BranchGroupPerm> branchGroupPerms = branchGroupPermRepo.findAllByBranchAndInherited(branch, true);
        branchGroupPermRepo.deleteAll(branchGroupPerms);
        responseBuilder.getGroups().insertPermissionUpdates_BranchGroupPerm(PermissionUpdateResponse.Action.REMOVE, branchGroupPerms);

        if (branch.isInherit()) {
            for (ProjectUserPerm p: projectUserPermRepo.findAllByProject(branch.getProject())) {
                BranchUserPerm perm = new BranchUserPerm(branch, p.getUser(), p.getRole(), true);
                branchUserPermRepo.save(perm);
                responseBuilder.getUsers().insertPermissionUpdate(PermissionUpdateResponse.Action.ADD, perm);
            }
            for (ProjectGroupPerm p: projectGroupPermRepo.findAllByProject(branch.getProject())) {
                BranchGroupPerm perm = new BranchGroupPerm(branch, p.getGroup(), p.getRole(), true);
                branchGroupPermRepo.save(perm);
                responseBuilder.getGroups().insertPermissionUpdate(PermissionUpdateResponse.Action.ADD, perm);
            }
        } else {
            for (ProjectUserPerm p: projectUserPermRepo.findAllByProjectAndRole_Name(branch.getProject(), AuthorizationConstants.ADMIN)) {
                BranchUserPerm perm = new BranchUserPerm(branch, p.getUser(), p.getRole(), true);
                branchUserPermRepo.save(perm);
                responseBuilder.getUsers().insertPermissionUpdate(PermissionUpdateResponse.Action.ADD, perm);
            }
            for (ProjectGroupPerm p: projectGroupPermRepo.findAllByProjectAndRole_Name(branch.getProject(), AuthorizationConstants.ADMIN)) {
                BranchGroupPerm perm = new BranchGroupPerm(branch, p.getGroup(), p.getRole(), true);
                branchGroupPermRepo.save(perm);
                responseBuilder.getGroups().insertPermissionUpdate(PermissionUpdateResponse.Action.ADD, perm);
            }
        }
        return responseBuilder.getPermissionUpdatesReponse();
    }
}
