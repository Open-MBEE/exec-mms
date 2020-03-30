package org.openmbee.sdvc.permissions;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import javax.transaction.Transactional;

import org.openmbee.sdvc.core.builders.PermissionUpdateResponseBuilder;
import org.openmbee.sdvc.core.config.AuthorizationConstants;
import org.openmbee.sdvc.core.config.Constants;
import org.openmbee.sdvc.core.delegation.PermissionsDelegateFactory;
import org.openmbee.sdvc.core.objects.PermissionResponse;
import org.openmbee.sdvc.core.objects.PermissionUpdateRequest;
import org.openmbee.sdvc.core.objects.PermissionUpdateResponse;
import org.openmbee.sdvc.core.services.PermissionService;
import org.openmbee.sdvc.data.domains.global.Branch;
import org.openmbee.sdvc.data.domains.global.Organization;
import org.openmbee.sdvc.data.domains.global.Project;
import org.openmbee.sdvc.core.delegation.PermissionsDelegate;
import org.openmbee.sdvc.permissions.exceptions.PermissionException;
import org.openmbee.sdvc.rdb.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service("defaultPermissionService")
public class DefaultPermissionService implements PermissionService {

    private BranchRepository branchRepo;
    private ProjectRepository projectRepo;
    private OrganizationRepository orgRepo;
    private List<PermissionsDelegateFactory> permissionsDelegateFactories;

    @Autowired
    public void setPermissionsDelegateFactories(List<PermissionsDelegateFactory> permissionsDelegateFactories) {
        this.permissionsDelegateFactories = permissionsDelegateFactories;
    }

    @Autowired
    public void setBranchRepo(BranchRepository branchRepo) {
        this.branchRepo = branchRepo;
    }

    @Autowired
    public void setProjectRepo(ProjectRepository projectRepo) {
        this.projectRepo = projectRepo;
    }

    @Autowired
    public void setOrgRepo(OrganizationRepository orgRepo) {
        this.orgRepo = orgRepo;
    }

    @Override
    @Transactional
    public void initOrgPerms(String orgId, String creator) {
        Organization organization = getOrganization(orgId);
        PermissionsDelegate permissionsDelegate = getPermissionsDelegate(organization);
        permissionsDelegate.initializePermissions(creator);
    }

    @Override
    @Transactional
    public void initProjectPerms(String projectId, boolean inherit, String creator) {
        Project project = getProject(projectId);
        PermissionsDelegate permissionsDelegate = getPermissionsDelegate(project);
        permissionsDelegate.initializePermissions(creator, inherit);

        recalculateInheritedPerms(project);
        initBranchPerms(projectId, Constants.MASTER_BRANCH, true, creator);
    }

    @Override
    @Transactional
    public void initBranchPerms(String projectId, String branchId, boolean inherit, String creator) {
        Branch branch = getBranch(projectId, branchId, BRANCH_NOTFOUND_BEHAVIOR.CREATE);
        PermissionsDelegate permissionsDelegate = getPermissionsDelegate(branch);
        permissionsDelegate.initializePermissions(creator, inherit);

        recalculateInheritedPerms(branch);
    }

    @Override
    @Transactional
    public PermissionUpdateResponse updateOrgUserPerms(PermissionUpdateRequest req, String orgId) {
        Organization organization = getOrganization(orgId);
        PermissionsDelegate permissionsDelegate = getPermissionsDelegate(organization);
        PermissionUpdateResponseBuilder responseBuilder = new PermissionUpdateResponseBuilder();
        responseBuilder.add(permissionsDelegate.updateUserPermissions(req));

        for (Project proj: organization.getProjects()) {
            responseBuilder.add(recalculateInheritedPerms(proj));
        }

        return responseBuilder.getPermissionUpdateReponse();
    }

    @Override
    @Transactional
    public PermissionUpdateResponse updateOrgGroupPerms(PermissionUpdateRequest req, String orgId) {
        Organization organization = getOrganization(orgId);
        PermissionsDelegate permissionsDelegate = getPermissionsDelegate(organization);
        PermissionUpdateResponseBuilder responseBuilder = new PermissionUpdateResponseBuilder();
        responseBuilder.add(permissionsDelegate.updateGroupPermissions(req));

        for (Project proj : organization.getProjects()) {
            responseBuilder.add(recalculateInheritedPerms(proj));
        }

        return responseBuilder.getPermissionUpdateReponse();
    }

    @Override
    @Transactional
    public PermissionUpdateResponse updateProjectUserPerms(PermissionUpdateRequest req, String projectId) {
        Project project = getProject(projectId);
        PermissionsDelegate permissionsDelegate = getPermissionsDelegate(project);
        PermissionUpdateResponseBuilder responseBuilder = new PermissionUpdateResponseBuilder();
        responseBuilder.add(permissionsDelegate.updateUserPermissions(req));

        for (Branch b : project.getBranches()) {
            responseBuilder.add(recalculateInheritedPerms(b));
        }

        return responseBuilder.getPermissionUpdateReponse();
    }

    @Override
    @Transactional
    public PermissionUpdateResponse updateProjectGroupPerms(PermissionUpdateRequest req, String projectId) {
        Project project = getProject(projectId);
        PermissionsDelegate permissionsDelegate = getPermissionsDelegate(project);
        PermissionUpdateResponseBuilder responseBuilder = new PermissionUpdateResponseBuilder();
        responseBuilder.add(permissionsDelegate.updateGroupPermissions(req));

        for (Branch b : project.getBranches()) {
            responseBuilder.add(recalculateInheritedPerms(b));
        }

        return responseBuilder.getPermissionUpdateReponse();
    }

    @Override
    @Transactional
    public PermissionUpdateResponse updateBranchUserPerms(PermissionUpdateRequest req, String projectId, String branchId) {
        Branch branch = getBranch(projectId, branchId, BRANCH_NOTFOUND_BEHAVIOR.THROW);
        PermissionsDelegate permissionsDelegate = getPermissionsDelegate(branch);
        return permissionsDelegate.updateUserPermissions(req);
    }

    @Override
    @Transactional
    public PermissionUpdateResponse updateBranchGroupPerms(PermissionUpdateRequest req, String projectId, String branchId) {
        Branch branch = getBranch(projectId, branchId, BRANCH_NOTFOUND_BEHAVIOR.THROW);
        PermissionsDelegate permissionsDelegate = getPermissionsDelegate(branch);
        return permissionsDelegate.updateGroupPermissions(req);
    }

    @Override
    @Transactional
    public void setProjectInherit(boolean isInherit, String projectId) {
        Project project = getProject(projectId);
        PermissionsDelegate permissionsDelegate = getPermissionsDelegate(project);
        if (permissionsDelegate.setInherit(isInherit)) {
            recalculateInheritedPerms(project);
        }
    }

    @Override
    @Transactional
    public void setBranchInherit(boolean isInherit, String projectId, String branchId) {
        Branch branch = getBranch(projectId, branchId, BRANCH_NOTFOUND_BEHAVIOR.THROW);
        PermissionsDelegate permissionsDelegate = getPermissionsDelegate(branch);
        if (permissionsDelegate.setInherit(isInherit)) {
            recalculateInheritedPerms(branch);
        }
    }

    @Override
    @Transactional
    public void setOrgPublic(boolean isPublic, String orgId) {
        Organization organization = getOrganization(orgId);
        PermissionsDelegate permissionsDelegate = getPermissionsDelegate(organization);
        permissionsDelegate.setPublic(isPublic);
    }

    @Override
    @Transactional
    public void setProjectPublic(boolean isPublic, String projectId) {
        Project project = getProject(projectId);
        PermissionsDelegate permissionsDelegate = getPermissionsDelegate(project);
        permissionsDelegate.setPublic(isPublic);
    }

    @Override
    @Transactional
    public boolean hasOrgPrivilege(String privilege, String user, Set<String> groups, String orgId) {
        if (groups.contains(AuthorizationConstants.MMSADMIN)) return true;

        Organization organization = getOrganization(orgId);
        PermissionsDelegate permissionsDelegate = getPermissionsDelegate(organization);
        return permissionsDelegate.hasPermission(user, groups, privilege);
    }

    @Override
    @Transactional
    public boolean hasProjectPrivilege(String privilege, String user, Set<String> groups, String projectId) {
        if (groups.contains(AuthorizationConstants.MMSADMIN)) return true;

        Project project = getProject(projectId);
        PermissionsDelegate permissionsDelegate = getPermissionsDelegate(project);
        return permissionsDelegate.hasPermission(user, groups, privilege);
    }

    @Override
    @Transactional
    public boolean hasBranchPrivilege(String privilege, String user, Set<String> groups, String projectId, String branchId) {
        if (groups.contains(AuthorizationConstants.MMSADMIN)) return true;

        Branch branch = getBranch(projectId, branchId, BRANCH_NOTFOUND_BEHAVIOR.THROW);
        PermissionsDelegate permissionsDelegate = getPermissionsDelegate(branch);
        return permissionsDelegate.hasPermission(user, groups, privilege);
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
        Organization organization = getOrganization(orgId);
        PermissionsDelegate permissionsDelegate = getPermissionsDelegate(organization);
        return permissionsDelegate.getGroupRoles();
    }

    @Override
    @Transactional
    public PermissionResponse getOrgUserRoles(String orgId) {
        Organization organization = getOrganization(orgId);
        PermissionsDelegate permissionsDelegate = getPermissionsDelegate(organization);
        return permissionsDelegate.getUserRoles();
    }

    @Override
    @Transactional
    public PermissionResponse getProjectGroupRoles(String projectId) {
        Project project = getProject(projectId);
        PermissionsDelegate permissionsDelegate = getPermissionsDelegate(project);
        return permissionsDelegate.getGroupRoles();
    }

    @Override
    @Transactional
    public PermissionResponse getProjectUserRoles(String projectId) {
        Project project = getProject(projectId);
        PermissionsDelegate permissionsDelegate = getPermissionsDelegate(project);
        return permissionsDelegate.getUserRoles();
    }

    @Override
    @Transactional
    public PermissionResponse getBranchGroupRoles(String projectId, String branchId) {
        Branch branch = getBranch(projectId, branchId, BRANCH_NOTFOUND_BEHAVIOR.IGNORE);

        if (branch == null) {
            return PermissionResponse.getDefaultResponse();
        }

        PermissionsDelegate permissionsDelegate = getPermissionsDelegate(branch);
        return permissionsDelegate.getGroupRoles();
    }

    @Override
    @Transactional
    public PermissionResponse getBranchUserRoles(String projectId, String branchId) {
        Branch branch = getBranch(projectId, branchId, BRANCH_NOTFOUND_BEHAVIOR.IGNORE);

        if (branch == null) {
            return PermissionResponse.getDefaultResponse();
        }

        PermissionsDelegate permissionsDelegate = getPermissionsDelegate(branch);
        return permissionsDelegate.getUserRoles();
    }

    private PermissionUpdateResponse recalculateInheritedPerms(Project project) {

        PermissionsDelegate permissionsDelegate = getPermissionsDelegate(project);
        PermissionUpdateResponseBuilder responseBuilder = new PermissionUpdateResponseBuilder();
        responseBuilder.add(permissionsDelegate.recalculateInheritedPerms());

        if (project.getBranches() == null) { //TODO this shouldn't be returning null..
            return responseBuilder.getPermissionUpdateReponse();
        }
        for (Branch branch : project.getBranches()) {
            responseBuilder.add(recalculateInheritedPerms(branch));
        }
        return responseBuilder.getPermissionUpdateReponse();
    }

    private PermissionUpdateResponse recalculateInheritedPerms(Branch branch) {
        PermissionsDelegate permissionsDelegate = getPermissionsDelegate(branch);
        return permissionsDelegate.recalculateInheritedPerms();
    }

    private Organization getOrganization(String orgId) {
        Optional<Organization> org = orgRepo.findByOrganizationId(orgId);

        if (!org.isPresent()) {
            throw new PermissionException(HttpStatus.NOT_FOUND, "Organization not found");
        }

        return org.get();
    }

    private Project getProject(String projectId) {
        Optional<Project> proj = projectRepo.findByProjectId(projectId);

        if (!proj.isPresent()) {
            throw new PermissionException(HttpStatus.NOT_FOUND, "Project not found");
        }
        return proj.get();
    }

    private enum BRANCH_NOTFOUND_BEHAVIOR {THROW, CREATE, IGNORE}

    private Branch getBranch(String projectId, String branchId, BRANCH_NOTFOUND_BEHAVIOR mode) {
        Optional<Branch> branch = branchRepo.findByProject_ProjectIdAndBranchId(projectId, branchId);
        if(!branch.isPresent()) {
            switch(mode){
                case THROW:
                    throw new PermissionException(HttpStatus.NOT_FOUND, "Branch not found");
                case CREATE:
                    Branch b = new Branch(getProject(projectId), branchId, false);
                    branchRepo.save(b);
                    return b;
                default:
                    //do nothing
                    break;

            }
        }
        return branch.get();
    }

    private PermissionsDelegate getPermissionsDelegate(final Organization organization) {
        Optional<PermissionsDelegate> permissionsDelegate = permissionsDelegateFactories.stream()
            .map(v -> v.getPermissionsDelegate(organization)).filter(Objects::nonNull).findFirst();

        if(permissionsDelegate.isPresent()) {
            return permissionsDelegate.get();
        }

        throw new PermissionException(HttpStatus.INTERNAL_SERVER_ERROR,
            "No valid permissions scheme found for organization " + organization.getOrganizationId()
                + " (" + organization.getOrganizationName() + ")");
    }

    private PermissionsDelegate getPermissionsDelegate(final Project project) {
        Optional<PermissionsDelegate> permissionsDelegate = permissionsDelegateFactories.stream()
            .map(v -> v.getPermissionsDelegate(project)).filter(Objects::nonNull).findFirst();

        if(permissionsDelegate.isPresent()) {
            return permissionsDelegate.get();
        }

        throw new PermissionException(HttpStatus.INTERNAL_SERVER_ERROR,
            "No valid permissions scheme found for project " + project.getProjectId()
                + " (" + project.getProjectName() + ")");
    }

    private PermissionsDelegate getPermissionsDelegate(final Branch branch) {
        Optional<PermissionsDelegate> permissionsDelegate = permissionsDelegateFactories.stream()
            .map(v -> v.getPermissionsDelegate(branch)).filter(Objects::nonNull).findFirst();

        if(permissionsDelegate.isPresent()) {
            return permissionsDelegate.get();
        }

        throw new PermissionException(HttpStatus.INTERNAL_SERVER_ERROR,
            "No valid permissions scheme found for branch " + branch.getBranchId()
                + " of project " + branch.getProject().getProjectId()
                + " (" + branch.getProject().getProjectName() + ")");
    }
}
