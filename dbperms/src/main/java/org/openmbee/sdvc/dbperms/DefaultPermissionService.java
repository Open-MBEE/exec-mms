package org.openmbee.sdvc.dbperms;

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
import org.openmbee.sdvc.rdb.repositories.BranchGroupPermRepository;
import org.openmbee.sdvc.rdb.repositories.BranchRepository;
import org.openmbee.sdvc.rdb.repositories.BranchUserPermRepository;
import org.openmbee.sdvc.rdb.repositories.GroupRepository;
import org.openmbee.sdvc.rdb.repositories.OrgGroupPermRepository;
import org.openmbee.sdvc.rdb.repositories.OrgUserPermRepository;
import org.openmbee.sdvc.rdb.repositories.OrganizationRepository;
import org.openmbee.sdvc.rdb.repositories.ProjectGroupPermRepository;
import org.openmbee.sdvc.rdb.repositories.ProjectRepository;
import org.openmbee.sdvc.rdb.repositories.ProjectUserPermRepository;
import org.openmbee.sdvc.rdb.repositories.RoleRepository;
import org.openmbee.sdvc.rdb.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
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



    @Override
    public void initOrgPerms(String orgId, String creator) {
        Optional<User> user = userRepo.findByUsername(creator);
        Optional<Role> role = roleRepo.findByName("ADMIN");
        Optional<Organization> org = orgRepo.findByOrganizationId(orgId);
        if (!user.isPresent() || !role.isPresent() || !org.isPresent()) {
            //throw exception
        }
        OrgUserPerm perm = new OrgUserPerm(org.get(), user.get(), role.get());
        orgUserPermRepo.save(perm);
    }

    @Override
    public void initProjectPerms(String projectId, boolean inherit, String creator) {
        Optional<User> user = userRepo.findByUsername(creator);
        Optional<Role> role = roleRepo.findByName("ADMIN");
        Optional<Project> proj = projectRepo.findByProjectId(projectId);
        if (!user.isPresent() || !role.isPresent() || !proj.isPresent()) {
            //throw exception
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
        if (!user.isPresent() || !role.isPresent() || !b.isPresent()) {
            //throw exception
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
            //throw exception
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
                    //Optional<Role> role = roleRepo.findByName(p.getRole()); //role is irrelevant here?
                    if (!user.isPresent()) {
                        //throw exception or skip
                        continue;
                    }
                    Optional<OrgUserPerm> perm = orgUserPermRepo.findByOrganizationAndUser(o, user.get());
                    if (perm.isPresent()) {
                        orgUserPermRepo.delete(perm.get());
                    }
                }
                break;
        }
        for (Project proj: o.getProjects()) {
            recalculateInheritedPerms(proj);
        }
    }

    @Override
    public void updateOrgGroupPerms(PermissionUpdateRequest req, String orgId) {

    }

    @Override
    public void updateProjectUserPerms(PermissionUpdateRequest req, String projectId) {
        Optional<Project> project = projectRepo.findByProjectId(projectId);
        if (!project.isPresent()) {
            //throw exception
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
                    if (perm.isPresent()) {
                        projectUserPermRepo.delete(perm.get());
                    }
                }
                break;
        }
        for (Branch b: proj.getBranches()) {
            recalculateInheritedPerms(b);
        }
    }

    @Override
    public void updateProjectGroupPerms(PermissionUpdateRequest req, String projectId) {

    }

    @Override
    public void updateBranchUserPerms(PermissionUpdateRequest req, String projectId, String branchId) {

    }

    @Override
    public void updateBranchGroupPerms(PermissionUpdateRequest req, String projectId, String branchId) {

    }

    @Override
    public void setProjectInherit(boolean isInherit, String projectId) {

    }

    @Override
    public void setBranchInherit(boolean isInherit, String projectId, String branchId) {

    }

    @Override
    public void setOrgPublic(boolean isPublic, String orgId) {

    }

    @Override
    public void setProjectPublic(boolean isPublic, String projectId) {

    }

    @Override
    public boolean hasOrgPrivilege(String privilege, String user, String orgId) {
        Set<String> privileges = new HashSet<>();
        Optional<User> u = userRepo.findByUsername(user);
        Optional<Organization> o = orgRepo.findByOrganizationId(orgId);
        if (!u.isPresent() || !o.isPresent()) {
            //throw exception
        }
        Optional<OrgUserPerm> perm = orgUserPermRepo.findByOrganizationAndUser(o.get(), u.get());
        if (perm.isPresent()) {
            for (Privilege p: perm.get().getRole().getPrivileges()) {
                privileges.add(p.getName());
            }
        }
        //either use stored groups or get groups from sso or ldap or other means, should add a param so it can be passed in..
        for (Group g: u.get().getGroups()) {
            Optional<OrgGroupPerm> perm2 = orgGroupPermRepo.findByOrganizationAndGroup(o.get(), g);
            if (perm2.isPresent()) {
                for (Privilege p: perm2.get().getRole().getPrivileges()) {
                    privileges.add(p.getName());
                }
            }
        }
        return privileges.contains(privilege);
    }

    @Override
    public boolean hasProjectPrivilege(String privilege, String user, String projectId) {
        return false;
    }

    @Override
    public boolean hasBranchPrivilege(String privilege, String user, String projectId, String branchId) {
        return false;
    }

    @Override
    public boolean isProjectInherit(String projectId) {
        return false;
    }

    @Override
    public boolean isBranchInherit(String projectId, String branchId) {
        return false;
    }

    @Override
    public boolean isOrgPublic(String orgId) {
        return false;
    }

    @Override
    public boolean isProjectPublic(String projectId) {
        return false;
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
