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

public class DefaultBranchPermissionsDelegate extends AbstractDefaultPermissionsDelegate {

    @Autowired
    private BranchRepository branchRepo;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private GroupRepository groupRepo;

    @Autowired
    private RoleRepository roleRepo;

    @Autowired
    private BranchUserPermRepository branchUserPermRepo;

    @Autowired
    private BranchGroupPermRepository branchGroupPermRepo;

    @Autowired
    private PrivilegeRepository privRepo;

    @Autowired
    private ProjectGroupPermRepository projectGroupPermRepo;

    @Autowired
    private ProjectUserPermRepository projectUserPermRepo;

    private Branch branch;

    public DefaultBranchPermissionsDelegate(Branch branch) {
        this.branch = branch;
    }

    @Override
    public boolean hasPermission(String user, Set<String> groups, String privilege) {

        Optional<Privilege> priv = privRepo.findByName(privilege);
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

        Optional<User> user = userRepo.findByUsername(creator);
        Optional<Role> role = roleRepo.findByName("ADMIN");

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
    public void updateUserPermissions(PermissionUpdateRequest req) {
        switch(req.getAction()) {
            case MODIFY:
                for (PermissionUpdateRequest.Permission p: req.getPermissions()) {
                    Optional<User> user = userRepo.findByUsername(p.getName());
                    Optional<Role> role = roleRepo.findByName(p.getRole());
                    if (!user.isPresent() || !role.isPresent()) {
                        //throw exception or skip
                        continue;
                    }
                    Optional<BranchUserPerm> exist = branchUserPermRepo.findByBranchAndUserAndInheritedIsFalse(branch, user.get());
                    if (exist.isPresent()) {
                        BranchUserPerm e = exist.get();
                        if (!role.get().equals(e.getRole())) {
                            e.setRole(role.get());
                            branchUserPermRepo.save(e);
                        }
                    } else {
                        branchUserPermRepo.save(new BranchUserPerm(branch, user.get(), role.get(), false));
                    }
                }
                break;
            case REPLACE:
                branchUserPermRepo.deleteByBranchAndInherited(branch, false);
                for (PermissionUpdateRequest.Permission p: req.getPermissions()) {
                    Optional<User> user = userRepo.findByUsername(p.getName());
                    Optional<Role> role = roleRepo.findByName(p.getRole());
                    if (!user.isPresent() || !role.isPresent()) {
                        //throw exception or skip
                        continue;
                    }
                    branchUserPermRepo.save(new BranchUserPerm(branch, user.get(), role.get(), false));
                }
                break;
            case REMOVE:
                Set<String> users = new HashSet<>();
                req.getPermissions().forEach(p -> users.add(p.getName()));
                branchUserPermRepo.deleteByBranchAndUser_UsernameInAndInheritedIsFalse(branch, users);
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
                    Optional<BranchGroupPerm> exist = branchGroupPermRepo.findByBranchAndGroupAndInheritedIsFalse(branch, pair.getFirst());
                    if (exist.isPresent()) {
                        BranchGroupPerm e = exist.get();
                        if (!pair.getSecond().equals(e.getRole())) {
                            e.setRole(pair.getSecond());
                            branchGroupPermRepo.save(e);
                        }
                    } else {
                        branchGroupPermRepo.save(new BranchGroupPerm(branch, pair.getFirst(), pair.getSecond(), false));
                    }
                }
                break;
            case REPLACE:
                branchGroupPermRepo.deleteByBranchAndInherited(branch, false);
                for (PermissionUpdateRequest.Permission p: req.getPermissions()) {
                    Pair<Group, Role> pair = getGroupAndRole(p);
                    if (pair.getFirst() == null || pair.getSecond() == null) {
                        continue;
                    }
                    branchGroupPermRepo.save(new BranchGroupPerm(branch, pair.getFirst(), pair.getSecond(), false));
                }
                break;
            case REMOVE:
                Set<String> groups = new HashSet<>();
                req.getPermissions().forEach(p -> groups.add(p.getName()));
                branchGroupPermRepo.deleteByBranchAndGroup_NameInAndInheritedIsFalse(branch, groups);
                break;
        }
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
    public void recalculateInheritedPerms() {
        branchUserPermRepo.deleteAll(branchUserPermRepo.findAllByBranchAndInherited(branch, true));
        branchGroupPermRepo.deleteAll(branchGroupPermRepo.findAllByBranchAndInherited(branch, true));
        if (branch.isInherit()) {
            for (ProjectUserPerm p: projectUserPermRepo.findAllByProject(branch.getProject())) {
                branchUserPermRepo.save(new BranchUserPerm(branch, p.getUser(), p.getRole(), true));
            }
            for (ProjectGroupPerm p: projectGroupPermRepo.findAllByProject(branch.getProject())) {
                branchGroupPermRepo.save(new BranchGroupPerm(branch, p.getGroup(), p.getRole(), true));
            }
        } else {
            for (ProjectUserPerm p: projectUserPermRepo.findAllByProjectAndRole_Name(branch.getProject(), AuthorizationConstants.ADMIN)) {
                branchUserPermRepo.save(new BranchUserPerm(branch, p.getUser(), p.getRole(), true));
            }
            for (ProjectGroupPerm p: projectGroupPermRepo.findAllByProjectAndRole_Name(branch.getProject(), AuthorizationConstants.ADMIN)) {
                branchGroupPermRepo.save(new BranchGroupPerm(branch, p.getGroup(), p.getRole(), true));
            }
        }
    }

    @Override
    protected GroupRepository getGroupRepo() {
        return groupRepo;
    }

    @Override
    protected RoleRepository getRoleRepo() {
        return roleRepo;
    }
}
