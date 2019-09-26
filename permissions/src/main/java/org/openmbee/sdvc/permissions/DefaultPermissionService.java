package org.openmbee.sdvc.permissions;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.transaction.Transactional;
import org.openmbee.sdvc.core.objects.PermissionResponse;
import org.openmbee.sdvc.core.objects.PermissionUpdateRequest;
import org.openmbee.sdvc.core.objects.PermissionUpdateRequest.Permission;
import org.openmbee.sdvc.core.services.PermissionService;
import org.openmbee.sdvc.data.domains.global.Branch;
import org.openmbee.sdvc.data.domains.global.BranchGroupPerm;
import org.openmbee.sdvc.data.domains.global.BranchUserPerm;
import org.openmbee.sdvc.data.domains.global.Group;
import org.openmbee.sdvc.data.domains.global.OrgGroupPerm;
import org.openmbee.sdvc.data.domains.global.OrgUserPerm;
import org.openmbee.sdvc.data.domains.global.Organization;
import org.openmbee.sdvc.data.domains.global.Privilege;
import org.openmbee.sdvc.data.domains.global.Project;
import org.openmbee.sdvc.data.domains.global.ProjectGroupPerm;
import org.openmbee.sdvc.data.domains.global.ProjectUserPerm;
import org.openmbee.sdvc.data.domains.global.Role;
import org.openmbee.sdvc.data.domains.global.User;
import org.openmbee.sdvc.permissions.exceptions.PermissionException;
import org.openmbee.sdvc.rdb.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service("defaultPermissionService")
public class DefaultPermissionService implements PermissionService {

    private BranchRepository branchRepo;
    private BranchGroupPermRepository branchGroupPermRepo;
    private BranchUserPermRepository branchUserPermRepo;
    private ProjectRepository projectRepo;
    private ProjectGroupPermRepository projectGroupPermRepo;
    private ProjectUserPermRepository projectUserPermRepo;
    private OrganizationRepository orgRepo;
    private OrgGroupPermRepository orgGroupPermRepo;
    private OrgUserPermRepository orgUserPermRepo;
    private UserRepository userRepo;
    private GroupRepository groupRepo;
    private RoleRepository roleRepo;
    private PrivilegeRepository privRepo;

    @Autowired
    public void setBranchRepo(BranchRepository branchRepo) {
        this.branchRepo = branchRepo;
    }

    @Autowired
    public void setBranchGroupPermRepo(BranchGroupPermRepository branchGroupPermRepo) {
        this.branchGroupPermRepo = branchGroupPermRepo;
    }

    @Autowired
    public void setBranchUserPermRepo(
        BranchUserPermRepository branchUserPermRepo) {
        this.branchUserPermRepo = branchUserPermRepo;
    }

    @Autowired
    public void setProjectRepo(ProjectRepository projectRepo) {
        this.projectRepo = projectRepo;
    }

    @Autowired
    public void setProjectGroupPermRepo(ProjectGroupPermRepository projectGroupPermRepo) {
        this.projectGroupPermRepo = projectGroupPermRepo;
    }

    @Autowired
    public void setProjectUserPermRepo(ProjectUserPermRepository projectUserPermRepo) {
        this.projectUserPermRepo = projectUserPermRepo;
    }

    @Autowired
    public void setOrgRepo(OrganizationRepository orgRepo) {
        this.orgRepo = orgRepo;
    }

    @Autowired
    public void setOrgGroupPermRepo(OrgGroupPermRepository orgGroupPermRepo) {
        this.orgGroupPermRepo = orgGroupPermRepo;
    }

    @Autowired
    public void setOrgUserPermRepo(OrgUserPermRepository orgUserPermRepo) {
        this.orgUserPermRepo = orgUserPermRepo;
    }

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

    @Override
    @Transactional
    public void initOrgPerms(String orgId, String creator) {
        Optional<User> user = userRepo.findByUsername(creator);
        Optional<Role> role = roleRepo.findByName("ADMIN");
        Optional<Organization> org = orgRepo.findByOrganizationId(orgId);

        if (!user.isPresent()) {
            throw new PermissionException(HttpStatus.NOT_FOUND, "User not found");
        } else if (!role.isPresent()) {
            throw new PermissionException(HttpStatus.NOT_FOUND, "Role not found");
        } else if (!org.isPresent()) {
            throw new PermissionException(HttpStatus.NOT_FOUND, "Organization not found");
        }

        OrgUserPerm perm = new OrgUserPerm(org.get(), user.get(), role.get());
        orgUserPermRepo.save(perm);
    }

    @Override
    @Transactional
    public void initProjectPerms(String projectId, boolean inherit, String creator) {
        Optional<User> user = userRepo.findByUsername(creator);
        Optional<Role> role = roleRepo.findByName("ADMIN");
        Optional<Project> proj = projectRepo.findByProjectId(projectId);

        if (!user.isPresent()) {
            throw new PermissionException(HttpStatus.NOT_FOUND, "User not found");
        } else if (!role.isPresent()) {
            throw new PermissionException(HttpStatus.NOT_FOUND, "Role not found");
        } else if (!proj.isPresent()) {
            throw new PermissionException(HttpStatus.NOT_FOUND, "Project not found");
        }

        ProjectUserPerm perm = new ProjectUserPerm(proj.get(), user.get(), role.get(), false);
        projectUserPermRepo.save(perm);
        proj.get().setInherit(inherit);
        projectRepo.save(proj.get());

        recalculateInheritedPerms(proj.get());
        initBranchPerms(projectId, "master", true, creator);
    }

    @Override
    @Transactional
    public void initBranchPerms(String projectId, String branchId, boolean inherit, String creator) {
        Optional<User> user = userRepo.findByUsername(creator);
        Optional<Role> role = roleRepo.findByName("ADMIN");
        Optional<Branch> b = branchRepo.findByProject_ProjectIdAndBranchId(projectId, branchId);
        Optional<Project> p = projectRepo.findByProjectId(projectId);

        if (!user.isPresent()) {
            throw new PermissionException(HttpStatus.NOT_FOUND, "User not found");
        } else if (!role.isPresent()) {
            throw new PermissionException(HttpStatus.NOT_FOUND, "Role not found");
        } else if (!p.isPresent()) {
            throw new PermissionException(HttpStatus.NOT_FOUND, "Project not found");
        }
        Branch branch = b.orElse(new Branch(p.get(), branchId, inherit));
        branch.setInherit(inherit);
        branchRepo.save(branch);

        BranchUserPerm perm = new BranchUserPerm(branch, user.get(), role.get(), false);
        branchUserPermRepo.save(perm);
        recalculateInheritedPerms(branch);
    }

    @Override
    @Transactional
    public void updateOrgUserPerms(PermissionUpdateRequest req, String orgId) {
        Optional<Organization> org = orgRepo.findByOrganizationId(orgId);

        if (!org.isPresent()) {
            throw new PermissionException(HttpStatus.NOT_FOUND, "Organization not found");
        }

        Organization o = org.get();
        switch(req.getAction()) {
            case MODIFY:
                for (Permission p: req.getPermissions()) {
                    Optional<User> user = userRepo.findByUsername(p.getName());
                    Optional<Role> role = roleRepo.findByName(p.getRole());
                    if (!user.isPresent() || !role.isPresent()) {
                        //throw exception or skip
                        continue;
                    }
                    Optional<OrgUserPerm> exist = orgUserPermRepo.findByOrganizationAndUser(o, user.get());
                    if (exist.isPresent()) {
                        OrgUserPerm e = exist.get();
                        if (!role.get().equals(e.getRole())) {
                            e.setRole(role.get());
                            orgUserPermRepo.save(e);
                        }
                    } else {
                        orgUserPermRepo.save(new OrgUserPerm(o, user.get(), role.get()));
                    }
                }
                break;
            case REPLACE:
                orgUserPermRepo.deleteAll(orgUserPermRepo.findAllByOrganization(o));
                for (Permission p: req.getPermissions()) {
                    Optional<User> user = userRepo.findByUsername(p.getName());
                    Optional<Role> role = roleRepo.findByName(p.getRole());
                    if (!user.isPresent() || !role.isPresent()) {
                        //throw exception or skip
                        continue;
                    }
                    orgUserPermRepo.save(new OrgUserPerm(o, user.get(), role.get()));
                }
                break;
            case REMOVE:
                Set<String> users = new HashSet<>();
                req.getPermissions().forEach(p -> users.add(p.getName()));
                orgUserPermRepo.deleteByOrganizationAndUser_UsernameIn(o, users);
                break;
        }
        for (Project proj: o.getProjects()) {
            recalculateInheritedPerms(proj);
        }
    }

    @Override
    @Transactional
    public void updateOrgGroupPerms(PermissionUpdateRequest req, String orgId) {
        Optional<Organization> org = orgRepo.findByOrganizationId(orgId);

        if (!org.isPresent()) {
            throw new PermissionException(HttpStatus.NOT_FOUND, "Organization not found");
        }

        Organization o = org.get();
        switch(req.getAction()) {
            case MODIFY:
                for (Permission p: req.getPermissions()) {
                    Pair<Group, Role> pair = getGroupAndRole(p);
                    if (pair.getFirst() == null || pair.getSecond() == null) {
                        continue;
                    }
                    Optional<OrgGroupPerm> exist = orgGroupPermRepo.findByOrganizationAndGroup(o, pair.getFirst());
                    if (exist.isPresent()) {
                        OrgGroupPerm e = exist.get();
                        if (!pair.getSecond().equals(e.getRole())) {
                            e.setRole(pair.getSecond());
                            orgGroupPermRepo.save(e);
                        }
                    } else {
                        orgGroupPermRepo.save(new OrgGroupPerm(o, pair.getFirst(), pair.getSecond()));
                    }
                }
                break;
            case REPLACE:
                orgGroupPermRepo.deleteAll(orgGroupPermRepo.findAllByOrganization(o));
                for (Permission p: req.getPermissions()) {
                    Pair<Group, Role> pair = getGroupAndRole(p);
                    if (pair.getFirst() == null || pair.getSecond() == null) {
                        continue;
                    }
                    orgGroupPermRepo.save(new OrgGroupPerm(o, pair.getFirst(), pair.getSecond()));
                }
                break;
            case REMOVE:
                Set<String> groups = new HashSet<>();
                req.getPermissions().forEach(p -> groups.add(p.getName()));
                orgGroupPermRepo.deleteByOrganizationAndGroup_NameIn(o, groups);
                break;
        }
        for (Project proj: o.getProjects()) {
            recalculateInheritedPerms(proj);
        }
    }

    @Override
    @Transactional
    public void updateProjectUserPerms(PermissionUpdateRequest req, String projectId) {
        Optional<Project> project = projectRepo.findByProjectId(projectId);

        if (!project.isPresent()) {
            throw new PermissionException(HttpStatus.NOT_FOUND, "Project not found");
        }

        Project proj = project.get();
        switch(req.getAction()) {
            case MODIFY:
                for (Permission p: req.getPermissions()) {
                    Optional<User> user = userRepo.findByUsername(p.getName());
                    Optional<Role> role = roleRepo.findByName(p.getRole());
                    if (!user.isPresent() || !role.isPresent()) {
                        //throw exception or skip
                        continue;
                    }
                    Optional<ProjectUserPerm> exist = projectUserPermRepo.findByProjectAndUserAndInheritedIsFalse(proj, user.get());
                    if (exist.isPresent()) {
                        ProjectUserPerm e = exist.get();
                        if (!role.get().equals(e.getRole())) {
                            e.setRole(role.get());
                            projectUserPermRepo.save(e);
                        }
                    } else {
                        projectUserPermRepo.save(new ProjectUserPerm(proj, user.get(), role.get(), false));
                    }
                }
                break;
            case REPLACE:
                projectUserPermRepo.deleteAll(projectUserPermRepo.findAllByProjectAndInherited(proj, false));
                for (Permission p: req.getPermissions()) {
                    Optional<User> user = userRepo.findByUsername(p.getName());
                    Optional<Role> role = roleRepo.findByName(p.getRole());
                    if (!user.isPresent() || !role.isPresent()) {
                        //throw exception or skip
                        continue;
                    }
                    projectUserPermRepo.save(new ProjectUserPerm(proj, user.get(), role.get(), false));
                }
                break;
            case REMOVE:
                Set<String> users = new HashSet<>();
                req.getPermissions().forEach(p -> users.add(p.getName()));
                projectUserPermRepo.deleteByProjectAndUser_UsernameInAndInheritedIsFalse(proj, users);
                break;
        }
        for (Branch b: proj.getBranches()) {
            recalculateInheritedPerms(b);
        }
    }

    @Override
    @Transactional
    public void updateProjectGroupPerms(PermissionUpdateRequest req, String projectId) {
        Optional<Project> project = projectRepo.findByProjectId(projectId);

        if (!project.isPresent()) {
            throw new PermissionException(HttpStatus.NOT_FOUND, "Project not found");
        }

        Project proj = project.get();
        switch(req.getAction()) {
            case MODIFY:
                for (Permission p: req.getPermissions()) {
                    Pair<Group, Role> pair = getGroupAndRole(p);
                    if (pair.getFirst() == null || pair.getSecond() == null) {
                        continue;
                    }
                    Optional<ProjectGroupPerm> exist = projectGroupPermRepo.findByProjectAndGroupAndInheritedIsFalse(proj, pair.getFirst());
                    if (exist.isPresent()) {
                        ProjectGroupPerm e = exist.get();
                        if (!pair.getSecond().equals(e.getRole())) {
                            e.setRole(pair.getSecond());
                            projectGroupPermRepo.save(e);
                        }
                    } else {
                        projectGroupPermRepo.save(new ProjectGroupPerm(proj, pair.getFirst(), pair.getSecond(), false));
                    }
                }
                break;
            case REPLACE:
                projectGroupPermRepo.deleteAll(projectGroupPermRepo.findAllByProjectAndInherited(proj, false));
                for (Permission p: req.getPermissions()) {
                    Pair<Group, Role> pair = getGroupAndRole(p);
                    if (pair.getFirst() == null || pair.getSecond() == null) {
                        continue;
                    }
                    projectGroupPermRepo.save(new ProjectGroupPerm(proj, pair.getFirst(), pair.getSecond(), false));
                }
                break;
            case REMOVE:
                Set<String> groups = new HashSet<>();
                req.getPermissions().forEach(p -> groups.add(p.getName()));
                projectGroupPermRepo.deleteByProjectAndGroup_NameInAndInheritedIsFalse(proj, groups);
                break;
        }
        for (Branch b: proj.getBranches()) {
            recalculateInheritedPerms(b);
        }
    }

    @Override
    @Transactional
    public void updateBranchUserPerms(PermissionUpdateRequest req, String projectId, String branchId) {
        Optional<Branch> branch = branchRepo.findByProject_ProjectIdAndBranchId(projectId, branchId);

        if (!branch.isPresent()) {
            throw new PermissionException(HttpStatus.NOT_FOUND, "Branch not found");
        }

        Branch bran = branch.get();
        switch(req.getAction()) {
            case MODIFY:
                for (Permission p: req.getPermissions()) {
                    Optional<User> user = userRepo.findByUsername(p.getName());
                    Optional<Role> role = roleRepo.findByName(p.getRole());
                    if (!user.isPresent() || !role.isPresent()) {
                        //throw exception or skip
                        continue;
                    }
                    Optional<BranchUserPerm> exist = branchUserPermRepo.findByBranchAndUserAndInheritedIsFalse(bran, user.get());
                    if (exist.isPresent()) {
                        BranchUserPerm e = exist.get();
                        if (!role.get().equals(e.getRole())) {
                            e.setRole(role.get());
                            branchUserPermRepo.save(e);
                        }
                    } else {
                        branchUserPermRepo.save(new BranchUserPerm(bran, user.get(), role.get(), false));
                    }
                }
                break;
            case REPLACE:
                branchUserPermRepo.deleteAll(branchUserPermRepo.findAllByBranchAndInherited(bran, false));
                for (Permission p: req.getPermissions()) {
                    Optional<User> user = userRepo.findByUsername(p.getName());
                    Optional<Role> role = roleRepo.findByName(p.getRole());
                    if (!user.isPresent() || !role.isPresent()) {
                        //throw exception or skip
                        continue;
                    }
                    branchUserPermRepo.save(new BranchUserPerm(bran, user.get(), role.get(), false));
                }
                break;
            case REMOVE:
                Set<String> users = new HashSet<>();
                req.getPermissions().forEach(p -> users.add(p.getName()));
                branchUserPermRepo.deleteByBranchAndUser_UsernameInAndInheritedIsFalse(bran, users);
                break;
        }
    }

    @Override
    @Transactional
    public void updateBranchGroupPerms(PermissionUpdateRequest req, String projectId, String branchId) {
        Optional<Branch> branchOptional = branchRepo.findByProject_ProjectIdAndBranchId(projectId, branchId);

        if (!branchOptional.isPresent()) {
            throw new PermissionException(HttpStatus.NOT_FOUND, "Branch not found");
        }

        Branch branch = branchOptional.get();
        switch(req.getAction()) {
            case MODIFY:
                for (Permission p: req.getPermissions()) {
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
                branchGroupPermRepo.deleteAll(branchGroupPermRepo.findAllByBranchAndInherited(branch, false));
                for (Permission p: req.getPermissions()) {
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
    @Transactional
    public void setProjectInherit(boolean isInherit, String projectId) {
        Optional<Project> project = projectRepo.findByProjectId(projectId);

        if (!project.isPresent()) {
            throw new PermissionException(HttpStatus.NOT_FOUND, "Project not found");
        }

        Project proj = project.get();
        proj.setInherit(isInherit);
        projectRepo.save(proj);
        recalculateInheritedPerms(proj);
    }

    @Override
    @Transactional
    public void setBranchInherit(boolean isInherit, String projectId, String branchId) {
        Optional<Branch> branch = branchRepo.findByProject_ProjectIdAndBranchId(projectId, branchId);

        if (!branch.isPresent()) {
            throw new PermissionException(HttpStatus.NOT_FOUND, "Branch not found");
        }

        Branch bran = branch.get();
        bran.setInherit(isInherit);
        branchRepo.save(bran);
        recalculateInheritedPerms(bran);
    }

    @Override
    @Transactional
    public void setOrgPublic(boolean isPublic, String orgId) {
        Optional<Organization> organization = orgRepo.findByOrganizationId(orgId);

        if (!organization.isPresent()) {
            throw new PermissionException(HttpStatus.NOT_FOUND, "Organization not found");
        }

        Organization org = organization.get();
        org.setPublic(isPublic);
        orgRepo.save(org);
    }

    @Override
    @Transactional
    public void setProjectPublic(boolean isPublic, String projectId) {
        Optional<Project> project = projectRepo.findByProjectId(projectId);

        if (!project.isPresent()) {
            throw new PermissionException(HttpStatus.NOT_FOUND, "Project not found");
        }

        Project proj = project.get();
        proj.setPublic(isPublic);
        projectRepo.save(proj);
    }

    @Override
    @Transactional
    public boolean hasOrgPrivilege(String privilege, String user, Set<String> groups, String orgId) {
        Optional<Privilege> priv = privRepo.findByName(privilege);
        if (!priv.isPresent()) {
            throw new PermissionException(HttpStatus.BAD_REQUEST, "No such privilege");
        }

        Optional<Organization> organization = orgRepo.findByOrganizationId(orgId);
        if (!organization.isPresent()) {
            throw new PermissionException(HttpStatus.NOT_FOUND, "Organization not found");
        }
        Set<Role> roles = priv.get().getRoles();
        if (orgUserPermRepo.existsByOrganizationAndUser_UsernameAndRoleIn(organization.get(), user, roles)) {
            return true;
        }
        if (orgGroupPermRepo.existsByOrganizationAndGroup_NameInAndRoleIn(organization.get(), groups, roles)) {
            return true;
        }
        return false;
    }

    @Override
    @Transactional
    public boolean hasProjectPrivilege(String privilege, String user, Set<String> groups, String projectId) {
        Optional<Privilege> priv = privRepo.findByName(privilege);
        if (!priv.isPresent()) {
            throw new PermissionException(HttpStatus.BAD_REQUEST, "No such privilege");
        }
        Optional<Project> project = projectRepo.findByProjectId(projectId);
        if (!project.isPresent()) {
            throw new PermissionException(HttpStatus.NOT_FOUND, "Project not found");
        }
        Set<Role> roles = priv.get().getRoles();
        if (projectUserPermRepo.existsByProjectAndUser_UsernameAndRoleIn(project.get(), user, roles)) {
            return true;
        }

        if (projectGroupPermRepo.existsByProjectAndGroup_NameInAndRoleIn(project.get(), groups, roles)) {
            return true;
        }
        return false;
    }

    @Override
    @Transactional
    public boolean hasBranchPrivilege(String privilege, String user, Set<String> groups, String projectId, String branchId) {
        Optional<Privilege> priv = privRepo.findByName(privilege);
        if (!priv.isPresent()) {
            throw new PermissionException(HttpStatus.NOT_FOUND, "No such privilege");
        }

        Optional<Branch> branch = branchRepo.findByProject_ProjectIdAndBranchId(projectId, branchId);
        if (!branch.isPresent()) {
            throw new PermissionException(HttpStatus.NOT_FOUND, "Branch not found");
        }
        Set<Role> roles = priv.get().getRoles();
        if (branchUserPermRepo.existsByBranchAndUser_UsernameAndRoleIn(branch.get(), user, roles)) {
            return true;
        }
        if (branchGroupPermRepo.existsByBranchAndGroup_NameInAndRoleIn(branch.get(), groups, roles)) {
            return true;
        }
        return false;
    }

    @Override
    @Transactional
    public boolean isProjectInherit(String projectId) {
        Optional<Project> project = projectRepo.findByProjectId(projectId);
        return project.map(Project::isInherit).orElse(false);
    }

    @Override
    @Transactional
    public boolean isBranchInherit(String projectId, String branchId) {
        Optional<Branch> branch = branchRepo.findByProject_ProjectIdAndBranchId(projectId, branchId);
        return branch.map(Branch::isInherit).orElse(false);
    }

    @Override
    @Transactional
    public boolean isOrgPublic(String orgId) {
        Optional<Organization> organization = orgRepo.findByOrganizationId(orgId);
        return organization.map(Organization::isPublic).orElse(false);
    }

    @Override
    @Transactional
    public boolean isProjectPublic(String projectId) {
        Optional<Project> project = projectRepo.findByProjectId(projectId);
        return project.map(Project::isPublic).orElse(false);
    }

    @Override
    @Transactional
    public PermissionResponse getOrgGroupRoles(String orgId) {
        PermissionResponse res = initResponse();
        for (OrgGroupPerm perm: orgGroupPermRepo.findAllByOrganization_OrganizationId(orgId)) {
            res.getPermissions().add(new PermissionResponse.Permission(
                perm.getGroup().getName(),
                perm.getRole().getName(),
                false
            ));
        }
        return res;
    }

    @Override
    @Transactional
    public PermissionResponse getOrgUserRoles(String orgId) {
        PermissionResponse res = initResponse();
        for (OrgUserPerm perm: orgUserPermRepo.findAllByOrganization_OrganizationId(orgId)) {
            res.getPermissions().add(new PermissionResponse.Permission(
                perm.getUser().getUsername(),
                perm.getRole().getName(),
                false
            ));
        }
        return res;
    }

    @Override
    @Transactional
    public PermissionResponse getProjectGroupRoles(String projectId) {
        PermissionResponse res = initResponse();
        for (ProjectGroupPerm perm: projectGroupPermRepo.findAllByProject_ProjectId(projectId)) {
            res.getPermissions().add(new PermissionResponse.Permission(
                perm.getGroup().getName(),
                perm.getRole().getName(),
                perm.isInherited()
            ));
        }
        return res;
    }

    @Override
    @Transactional
    public PermissionResponse getProjectUserRoles(String projectId) {
        PermissionResponse res = initResponse();
        for (ProjectUserPerm perm: projectUserPermRepo.findAllByProject_ProjectId(projectId)) {
            res.getPermissions().add(new PermissionResponse.Permission(
                perm.getUser().getUsername(),
                perm.getRole().getName(),
                perm.isInherited()
            ));
        }
        return res;
    }

    @Override
    @Transactional
    public PermissionResponse getBranchGroupRoles(String projectId, String branchId) {
        PermissionResponse res = initResponse();
        Optional<Branch> b = branchRepo.findByProject_ProjectIdAndBranchId(projectId, branchId);
        if (!b.isPresent()) {
            return res;
        }
        for (BranchGroupPerm perm: branchGroupPermRepo.findAllByBranch(b.get())) {
            res.getPermissions().add(new PermissionResponse.Permission(
                perm.getGroup().getName(),
                perm.getRole().getName(),
                perm.isInherited()
            ));
        }
        return res;
    }

    @Override
    @Transactional
    public PermissionResponse getBranchUserRoles(String projectId, String branchId) {
        PermissionResponse res = initResponse();
        Optional<Branch> b = branchRepo.findByProject_ProjectIdAndBranchId(projectId, branchId);
        if (!b.isPresent()) {
            return res;
        }
        for (BranchUserPerm perm: branchUserPermRepo.findAllByBranch(b.get())) {
            res.getPermissions().add(new PermissionResponse.Permission(
                perm.getUser().getUsername(),
                perm.getRole().getName(),
                perm.isInherited()
            ));
        }
        return res;
    }

    private Pair<Group, Role> getGroupAndRole(Permission p) {
        Optional<Group> group = groupRepo.findByName(p.getName());
        Optional<Role> role = roleRepo.findByName(p.getRole());
        if (!role.isPresent()) {
            return Pair.of(group.orElse(null), null);
        }
        if (!group.isPresent()) {
            group = Optional.of(new Group(p.getName()));
            groupRepo.save(group.get());
        }
        return Pair.of(group.get(), role.get());
    }

    private PermissionResponse initResponse() {
        PermissionResponse res = new PermissionResponse();
        List<PermissionResponse.Permission> perms = new ArrayList<>();
        res.setPermissions(perms);
        return res;
    }

    private void recalculateInheritedPerms(Project project) {
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
            for (OrgUserPerm p: orgUserPermRepo.findAllByOrganizationAndRole_Name(project.getOrganization(), "ADMIN")) {
                projectUserPermRepo.save(new ProjectUserPerm(project, p.getUser(), p.getRole(), true));
            }
            for (OrgGroupPerm p: orgGroupPermRepo.findAllByOrganizationAndRole_Name(project.getOrganization(), "ADMIN")) {
                projectGroupPermRepo.save(new ProjectGroupPerm(project, p.getGroup(), p.getRole(), true));
            }
        }
        if (project.getBranches() == null) { //TODO this shouldn't be returning null..
            return;
        }
        for (Branch b: project.getBranches()) {
            recalculateInheritedPerms(b);
        }
    }

    private void recalculateInheritedPerms(Branch branch) {
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
            for (ProjectUserPerm p: projectUserPermRepo.findAllByProjectAndRole_Name(branch.getProject(), "ADMIN")) {
                branchUserPermRepo.save(new BranchUserPerm(branch, p.getUser(), p.getRole(), true));
            }
            for (ProjectGroupPerm p: projectGroupPermRepo.findAllByProjectAndRole_Name(branch.getProject(), "ADMIN")) {
                branchGroupPermRepo.save(new BranchGroupPerm(branch, p.getGroup(), p.getRole(), true));
            }
        }
    }
}
