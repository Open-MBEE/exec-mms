package org.openmbee.sdvc.permissions;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

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
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
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
        //auto create global master branch if not already there
        Optional<Branch> master = branchRepo.findByProject_ProjectIdAndBranchId(projectId, "master");
        Branch m;
        if (!master.isPresent()) {
            m = new Branch(proj.get(), "master", true);
            branchRepo.save(m);
        } else {
            m = master.get();
        }
        branchUserPermRepo.save(new BranchUserPerm(m, user.get(), role.get(), false));
        recalculateInheritedPerms(proj.get());
    }

    @Override
    public void initBranchPerms(String projectId, String branchId, boolean inherit, String creator) {
        Optional<User> user = userRepo.findByUsername(creator);
        Optional<Role> role = roleRepo.findByName("ADMIN");
        Optional<Branch> b = branchRepo.findByProject_ProjectIdAndBranchId(projectId, branchId);

        if (!user.isPresent()) {
            throw new PermissionException(HttpStatus.NOT_FOUND, "User not found");
        } else if (!role.isPresent()) {
            throw new PermissionException(HttpStatus.NOT_FOUND, "Role not found");
        } else if (!b.isPresent()) {
            throw new PermissionException(HttpStatus.NOT_FOUND, "Branch not found");
        }

        BranchUserPerm perm = new BranchUserPerm(b.get(), user.get(), role.get(), false);
        branchUserPermRepo.save(perm);
        b.get().setInherit(inherit);
        recalculateInheritedPerms(b.get());
    }

    @Override
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
                for (Permission p: req.getPermissions()) {
                    Optional<User> user = userRepo.findByUsername(p.getName());
                    if (!user.isPresent()) {
                        //throw exception or skip
                        continue;
                    }
                    Optional<OrgUserPerm> perm = orgUserPermRepo.findByOrganizationAndUser(o, user.get());
                    perm.ifPresent(orgUserPerm -> orgUserPermRepo.delete(orgUserPerm));
                }
                break;
        }
        for (Project proj: o.getProjects()) {
            recalculateInheritedPerms(proj);
        }
    }

    @Override
    public void updateOrgGroupPerms(PermissionUpdateRequest req, String orgId) {
        Optional<Organization> org = orgRepo.findByOrganizationId(orgId);

        if (!org.isPresent()) {
            throw new PermissionException(HttpStatus.NOT_FOUND, "Organization not found");
        }

        Organization o = org.get();
        switch(req.getAction()) {
            case MODIFY:
                for (Permission p: req.getPermissions()) {
                    Optional<Group> group = groupRepo.findByName(p.getName());
                    Optional<Role> role = roleRepo.findByName(p.getRole());
                    if (!group.isPresent() || !role.isPresent()) {
                        //throw exception or skip
                        continue;
                    }
                    Optional<OrgGroupPerm> exist = orgGroupPermRepo.findByOrganizationAndGroup(o, group.get());
                    if (exist.isPresent()) {
                        OrgGroupPerm e = exist.get();
                        if (!role.get().equals(e.getRole())) {
                            e.setRole(role.get());
                            orgGroupPermRepo.save(e);
                        }
                    } else {
                        orgGroupPermRepo.save(new OrgGroupPerm(o, group.get(), role.get()));
                    }
                }
                break;
            case REPLACE:
                orgGroupPermRepo.deleteAll(orgGroupPermRepo.findAllByOrganization(o));
                for (Permission p: req.getPermissions()) {
                    Optional<Group> group = groupRepo.findByName(p.getName());
                    Optional<Role> role = roleRepo.findByName(p.getRole());
                    if (!group.isPresent() || !role.isPresent()) {
                        //throw exception or skip
                        continue;
                    }
                    orgGroupPermRepo.save(new OrgGroupPerm(o, group.get(), role.get()));
                }
                break;
            case REMOVE:
                for (Permission p: req.getPermissions()) {
                    Optional<Group> group = groupRepo.findByName(p.getName());
                    //Optional<Role> role = roleRepo.findByName(p.getRole()); //role is irrelevant here?
                    if (!group.isPresent()) {
                        //throw exception or skip
                        continue;
                    }
                    Optional<OrgGroupPerm> perm = orgGroupPermRepo.findByOrganizationAndGroup(o, group.get());
                    perm.ifPresent(orgGroupPerm -> orgGroupPermRepo.delete(orgGroupPerm));
                }
                break;
        }
        for (Project proj: o.getProjects()) {
            recalculateInheritedPerms(proj);
        }
    }

    @Override
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
                    Optional<ProjectUserPerm> exist = projectUserPermRepo.findByProjectAndUserAndInherited(proj, user.get(), false);
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
                for (Permission p: req.getPermissions()) {
                    Optional<User> user = userRepo.findByUsername(p.getName());
                    //Optional<Role> role = roleRepo.findByName(p.getRole()); //role is irrelevant here?
                    if (!user.isPresent()) {
                        //throw exception or skip
                        continue;
                    }
                    Optional<ProjectUserPerm> perm = projectUserPermRepo.findByProjectAndUserAndInherited(proj, user.get(), false);
                    perm.ifPresent(projectUserPerm -> projectUserPermRepo.delete(projectUserPerm));
                }
                break;
        }
        for (Branch b: proj.getBranches()) {
            recalculateInheritedPerms(b);
        }
    }

    @Override
    public void updateProjectGroupPerms(PermissionUpdateRequest req, String projectId) {
        Optional<Project> project = projectRepo.findByProjectId(projectId);

        if (!project.isPresent()) {
            throw new PermissionException(HttpStatus.NOT_FOUND, "Project not found");
        }

        Project proj = project.get();
        switch(req.getAction()) {
            case MODIFY:
                for (Permission p: req.getPermissions()) {
                    Optional<Group> group = groupRepo.findByName(p.getName());
                    Optional<Role> role = roleRepo.findByName(p.getRole());
                    if (!group.isPresent() || !role.isPresent()) {
                        //throw exception or skip
                        continue;
                    }
                    Optional<ProjectGroupPerm> exist = projectGroupPermRepo.findByProjectAndGroupAndInherited(proj, group.get(), false);
                    if (exist.isPresent()) {
                        ProjectGroupPerm e = exist.get();
                        if (!role.get().equals(e.getRole())) {
                            e.setRole(role.get());
                            projectGroupPermRepo.save(e);
                        }
                    } else {
                        projectGroupPermRepo.save(new ProjectGroupPerm(proj, group.get(), role.get(), false));
                    }
                }
                break;
            case REPLACE:
                projectGroupPermRepo.deleteAll(projectGroupPermRepo.findAllByProjectAndInherited(proj, false));
                for (Permission p: req.getPermissions()) {
                    Optional<Group> group = groupRepo.findByName(p.getName());
                    Optional<Role> role = roleRepo.findByName(p.getRole());
                    if (!group.isPresent() || !role.isPresent()) {
                        //throw exception or skip
                        continue;
                    }
                    projectGroupPermRepo.save(new ProjectGroupPerm(proj, group.get(), role.get(), false));
                }
                break;
            case REMOVE:
                for (Permission p: req.getPermissions()) {
                    Optional<Group> group = groupRepo.findByName(p.getName());
                    //Optional<Role> role = roleRepo.findByName(p.getRole()); //role is irrelevant here?
                    if (!group.isPresent()) {
                        //throw exception or skip
                        continue;
                    }
                    Optional<ProjectGroupPerm> perm = projectGroupPermRepo.findByProjectAndGroupAndInherited(proj, group.get(), false);
                    perm.ifPresent(projectGroupPerm -> projectGroupPermRepo.delete(projectGroupPerm));
                }
                break;
        }
        for (Branch b: proj.getBranches()) {
            recalculateInheritedPerms(b);
        }
    }

    @Override
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
                for (Permission p: req.getPermissions()) {
                    Optional<User> user = userRepo.findByUsername(p.getName());
                    //Optional<Role> role = roleRepo.findByName(p.getRole()); //role is irrelevant here?
                    if (!user.isPresent()) {
                        //throw exception or skip
                        continue;
                    }
                    Optional<BranchUserPerm> perm = branchUserPermRepo.findByBranchAndUserAndInheritedIsFalse(bran, user.get());
                    perm.ifPresent(branchUserPerm -> branchUserPermRepo.delete(branchUserPerm));
                }
                break;
        }
        recalculateInheritedPerms(bran);
    }

    @Override
    public void updateBranchGroupPerms(PermissionUpdateRequest req, String projectId, String branchId) {
        Optional<Branch> branchOptional = branchRepo.findByProject_ProjectIdAndBranchId(projectId, branchId);

        if (!branchOptional.isPresent()) {
            throw new PermissionException(HttpStatus.NOT_FOUND, "Branch not found");
        }

        Branch branch = branchOptional.get();
        switch(req.getAction()) {
            case MODIFY:
                for (Permission p: req.getPermissions()) {
                    Optional<Group> group = groupRepo.findByName(p.getName());
                    Optional<Role> role = roleRepo.findByName(p.getRole());
                    if (!group.isPresent() || !role.isPresent()) {
                        //throw exception or skip
                        continue;
                    }
                    Optional<BranchGroupPerm> exist = branchGroupPermRepo.findByBranchAndGroupAndInheritedIsFalse(branch, group.get());
                    if (exist.isPresent()) {
                        BranchGroupPerm e = exist.get();
                        if (!role.get().equals(e.getRole())) {
                            e.setRole(role.get());
                            branchGroupPermRepo.save(e);
                        }
                    } else {
                        branchGroupPermRepo.save(new BranchGroupPerm(branch, group.get(), role.get(), false));
                    }
                }
                break;
            case REPLACE:
                branchGroupPermRepo.deleteAll(branchGroupPermRepo.findAllByBranchAndInherited(branch, false));
                for (Permission p: req.getPermissions()) {
                    Optional<Group> group = groupRepo.findByName(p.getName());
                    Optional<Role> role = roleRepo.findByName(p.getRole());
                    if (!group.isPresent() || !role.isPresent()) {
                        //throw exception or skip
                        continue;
                    }
                    branchGroupPermRepo.save(new BranchGroupPerm(branch, group.get(), role.get(), false));
                }
                break;
            case REMOVE:
                for (Permission p: req.getPermissions()) {
                    Optional<Group> group = groupRepo.findByName(p.getName());
                    //Optional<Role> role = roleRepo.findByName(p.getRole()); //role is irrelevant here?
                    if (!group.isPresent()) {
                        //throw exception or skip
                        continue;
                    }
                    Optional<BranchGroupPerm> perm = branchGroupPermRepo.findByBranchAndGroupAndInheritedIsFalse(branch, group.get());
                    perm.ifPresent(branchGroupPerm -> branchGroupPermRepo.delete(branchGroupPerm));
                }
                break;
        }
        recalculateInheritedPerms(branch);
    }

    @Override
    public void setProjectInherit(boolean isInherit, String projectId) {
        Optional<Project> project = projectRepo.findByProjectId(projectId);

        if (!project.isPresent()) {
            throw new PermissionException(HttpStatus.NOT_FOUND, "Project not found");
        }

        Project proj = project.get();
        proj.setInherit(isInherit);
        projectRepo.save(proj);
    }

    @Override
    public void setBranchInherit(boolean isInherit, String projectId, String branchId) {
        Optional<Branch> branch = branchRepo.findByProject_ProjectIdAndBranchId(projectId, branchId);

        if (!branch.isPresent()) {
            throw new PermissionException(HttpStatus.NOT_FOUND, "Branch not found");
        }

        Branch bran = branch.get();
        bran.setInherit(isInherit);
        branchRepo.save(bran);
    }

    @Override
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
    public boolean hasOrgPrivilege(String privilege, String user, String orgId) {
        Optional<Privilege> priv = privRepo.findByName(privilege);
        if (!priv.isPresent()) {
            throw new PermissionException(HttpStatus.BAD_REQUEST, "No such privilege");
        }

        Optional<Organization> organization = orgRepo.findByOrganizationId(orgId);
        if (!organization.isPresent()) {
            throw new PermissionException(HttpStatus.NOT_FOUND, "Organization not found");
        }

        Set<OrgUserPerm> orgUserPerm = orgUserPermRepo.findAllByOrganizationAndUser_Username(organization.get(), user);
        Set<Privilege> privileges = new HashSet<>();
        Set<Group> groups = new HashSet<>();
        orgUserPerm.forEach(oup -> {
            privileges.addAll(oup.getRole().getPrivileges());
            groups.addAll(oup.getUser().getGroups());
        });

        if (privileges.contains(priv.get())) {
            return true;
        }

        Set<OrgGroupPerm> orgGroupPerm = orgGroupPermRepo.findAllByOrganizationAndGroupIn(organization.get(), groups);
        if (orgGroupPerm.isEmpty()) {
            return false;
        }

        orgGroupPerm.forEach(ogp -> {
            privileges.addAll(ogp.getRole().getPrivileges());
        });

        return privileges.contains(priv.get());
    }

    @Override
    public boolean hasProjectPrivilege(String privilege, String user, String projectId) {
        Optional<Privilege> priv = privRepo.findByName(privilege);
        if (!priv.isPresent()) {
            throw new PermissionException(HttpStatus.BAD_REQUEST, "No such privilege");
        }

        Optional<Project> project = projectRepo.findByProjectId(projectId);
        if (!project.isPresent()) {
            throw new PermissionException(HttpStatus.NOT_FOUND, "Project not found");
        }

        Set<ProjectUserPerm> projectUserPerm = projectUserPermRepo.findAllByProjectAndUser_Username(project.get(), user);
        Set<Privilege> privileges = new HashSet<>();
        Set<Group> groups = new HashSet<>();
        projectUserPerm.forEach(pup -> {
            privileges.addAll(pup.getRole().getPrivileges());
            groups.addAll(pup.getUser().getGroups());
        });

        if (privileges.contains(priv.get())) {
            return true;
        }

        Set<ProjectGroupPerm> projectGroupPerm = projectGroupPermRepo.findAllByProjectAndGroupIn(project.get(), groups);
        if (projectGroupPerm.isEmpty()) {
            return false;
        }

        projectGroupPerm.forEach(pgp -> {
            privileges.addAll(pgp.getRole().getPrivileges());
        });

        return privileges.contains(priv.get());
    }

    @Override
    public boolean hasBranchPrivilege(String privilege, String user, String projectId, String branchId) {
        Optional<Privilege> priv = privRepo.findByName(privilege);
        if (!priv.isPresent()) {
            throw new PermissionException(HttpStatus.NOT_FOUND, "No such privilege");
        }

        Optional<Branch> branch = branchRepo.findByProject_ProjectIdAndBranchId(projectId, branchId);
        if (!branch.isPresent()) {
            throw new PermissionException(HttpStatus.NOT_FOUND, "Branch not found");
        }

        Set<BranchUserPerm> branchUserPerm = branchUserPermRepo.findAllByBranchAndUser_Username(branch.get(), user);
        Set<Privilege> privileges = new HashSet<>();
        Set<Group> groups = new HashSet<>();
        branchUserPerm.forEach(bup -> {
            privileges.addAll(bup.getRole().getPrivileges());
            groups.addAll(bup.getUser().getGroups());
        });

        if (privileges.contains(priv.get())) {
            return true;
        }

        Set<BranchGroupPerm> branchGroupPerm = branchGroupPermRepo.findAllByBranchAndGroupIn(branch.get(), groups);
        if (branchGroupPerm.isEmpty()) {
            return false;
        }

        branchGroupPerm.forEach(bgp -> {
            privileges.addAll(bgp.getRole().getPrivileges());
        });

        return privileges.contains(priv.get());
    }

    @Override
    public boolean isProjectInherit(String projectId) {
        Optional<Project> project = projectRepo.findByProjectId(projectId);
        return project.map(Project::isInherit).orElse(false);
    }

    @Override
    public boolean isBranchInherit(String projectId, String branchId) {
        Optional<Branch> branch = branchRepo.findByProject_ProjectIdAndBranchId(projectId, branchId);
        return branch.map(Branch::isInherit).orElse(false);
    }

    @Override
    public boolean isOrgPublic(String orgId) {
        Optional<Organization> organization = orgRepo.findByOrganizationId(orgId);
        return organization.map(Organization::isPublic).orElse(false);
    }

    @Override
    public boolean isProjectPublic(String projectId) {
        Optional<Project> project = projectRepo.findByProjectId(projectId);
        return project.map(Project::isPublic).orElse(false);
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
