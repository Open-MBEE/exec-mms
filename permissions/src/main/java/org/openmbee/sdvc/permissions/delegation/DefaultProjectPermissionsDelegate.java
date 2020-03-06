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

public class DefaultProjectPermissionsDelegate extends AbstractDefaultPermissionsDelegate {

    @Autowired
    private ProjectUserPermRepository projectUserPermRepo;

    @Autowired
    private ProjectGroupPermRepository projectGroupPermRepo;

    @Autowired
    private PrivilegeRepository privRepo;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private RoleRepository roleRepo;

    @Autowired
    private ProjectRepository projectRepo;

    @Autowired
    private GroupRepository groupRepo;

    @Autowired
    private OrgGroupPermRepository orgGroupPermRepo;

    @Autowired
    private OrgUserPermRepository orgUserPermRepo;

    private Project project;

    public DefaultProjectPermissionsDelegate(Project project) {
        this.project = project;
    }

    @Override
    public boolean hasPermission(String user, Set<String> groups, String privilege) {

        Optional<Privilege> priv = privRepo.findByName(privilege);
        if (!priv.isPresent()) {
            throw new PermissionException(HttpStatus.BAD_REQUEST, "No such privilege");
        }

        Set<Role> roles = priv.get().getRoles();

        if (projectUserPermRepo.existsByProjectAndUser_UsernameAndRoleIn(project, user, roles)) {
            return true;
        }

        if (!groups.isEmpty() && projectGroupPermRepo.existsByProjectAndGroup_NameInAndRoleIn(project, groups, roles)) {
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

        ProjectUserPerm perm = new ProjectUserPerm(project, user.get(), role.get(), false);
        projectUserPermRepo.save(perm);
        project.setInherit(inherit);
        projectRepo.save(project);
    }

    @Override
    public boolean setInherit(boolean isInherit) {
        if (project.isInherit() != isInherit) {
            project.setInherit(isInherit);
            projectRepo.save(project);
            return true;
        }
        return false;
    }

    @Override
    public void setPublic(boolean isPublic) {
        project.setPublic(isPublic);
        projectRepo.save(project);
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
                    Optional<ProjectUserPerm> exist = projectUserPermRepo.findByProjectAndUserAndInheritedIsFalse(project, user.get());
                    if (exist.isPresent()) {
                        ProjectUserPerm e = exist.get();
                        if (!role.get().equals(e.getRole())) {
                            e.setRole(role.get());
                            projectUserPermRepo.save(e);
                        }
                    } else {
                        projectUserPermRepo.save(new ProjectUserPerm(project, user.get(), role.get(), false));
                    }
                }
                break;
            case REPLACE:
                projectUserPermRepo.deleteByProjectAndInherited(project, false);
                for (PermissionUpdateRequest.Permission p: req.getPermissions()) {
                    Optional<User> user = userRepo.findByUsername(p.getName());
                    Optional<Role> role = roleRepo.findByName(p.getRole());
                    if (!user.isPresent() || !role.isPresent()) {
                        //throw exception or skip
                        continue;
                    }
                    projectUserPermRepo.save(new ProjectUserPerm(project, user.get(), role.get(), false));
                }
                break;
            case REMOVE:
                Set<String> users = new HashSet<>();
                req.getPermissions().forEach(p -> users.add(p.getName()));
                projectUserPermRepo.deleteByProjectAndUser_UsernameInAndInheritedIsFalse(project, users);
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
                    Optional<ProjectGroupPerm> exist = projectGroupPermRepo.findByProjectAndGroupAndInheritedIsFalse(project, pair.getFirst());
                    if (exist.isPresent()) {
                        ProjectGroupPerm e = exist.get();
                        if (!pair.getSecond().equals(e.getRole())) {
                            e.setRole(pair.getSecond());
                            projectGroupPermRepo.save(e);
                        }
                    } else {
                        projectGroupPermRepo.save(new ProjectGroupPerm(project, pair.getFirst(), pair.getSecond(), false));
                    }
                }
                break;
            case REPLACE:
                projectGroupPermRepo.deleteByProjectAndInherited(project, false);
                for (PermissionUpdateRequest.Permission p: req.getPermissions()) {
                    Pair<Group, Role> pair = getGroupAndRole(p);
                    if (pair.getFirst() == null || pair.getSecond() == null) {
                        continue;
                    }
                    projectGroupPermRepo.save(new ProjectGroupPerm(project, pair.getFirst(), pair.getSecond(), false));
                }
                break;
            case REMOVE:
                Set<String> groups = new HashSet<>();
                req.getPermissions().forEach(p -> groups.add(p.getName()));
                projectGroupPermRepo.deleteByProjectAndGroup_NameInAndInheritedIsFalse(project, groups);
                break;
        }
    }

    @Override
    public PermissionResponse getUserRoles() {
        PermissionResponse res = PermissionResponse.getDefaultResponse();
        for (ProjectUserPerm perm: projectUserPermRepo.findAllByProject_ProjectId(project.getProjectId())) {
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
        for (ProjectGroupPerm perm: projectGroupPermRepo.findAllByProject_ProjectId(project.getProjectId())) {
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
        projectUserPermRepo.deleteAll(projectUserPermRepo.findAllByProjectAndInherited(project, true));
        projectGroupPermRepo.deleteAll(projectGroupPermRepo.findAllByProjectAndInherited(project, true));
        if (project.isInherit()) {
            for (OrgUserPerm p: orgUserPermRepo.findAllByOrganization(project.getOrganization())) {
                projectUserPermRepo.save(new ProjectUserPerm(project, p.getUser(), p.getRole(), true));
            }
            for (OrgGroupPerm p: orgGroupPermRepo.findAllByOrganization(project.getOrganization())) {
                projectGroupPermRepo.save(new ProjectGroupPerm(project, p.getGroup(), p.getRole(), true));
            }
        } else {
            for (OrgUserPerm p: orgUserPermRepo.findAllByOrganizationAndRole_Name(project.getOrganization(), AuthorizationConstants.ADMIN)) {
                projectUserPermRepo.save(new ProjectUserPerm(project, p.getUser(), p.getRole(), true));
            }
            for (OrgGroupPerm p: orgGroupPermRepo.findAllByOrganizationAndRole_Name(project.getOrganization(), AuthorizationConstants.ADMIN)) {
                projectGroupPermRepo.save(new ProjectGroupPerm(project, p.getGroup(), p.getRole(), true));
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
