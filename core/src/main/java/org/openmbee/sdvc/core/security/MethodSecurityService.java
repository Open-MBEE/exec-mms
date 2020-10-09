package org.openmbee.sdvc.core.security;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import org.openmbee.sdvc.core.config.ContextHolder;
import org.openmbee.sdvc.core.dao.BranchDAO;
import org.openmbee.sdvc.core.dao.OrgDAO;
import org.openmbee.sdvc.core.dao.ProjectDAO;
import org.openmbee.sdvc.core.exceptions.NotFoundException;
import org.openmbee.sdvc.core.services.PermissionService;
import org.openmbee.sdvc.core.utils.AuthenticationUtils;
import org.openmbee.sdvc.data.domains.global.Organization;
import org.openmbee.sdvc.data.domains.global.Project;
import org.openmbee.sdvc.data.domains.scoped.Branch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service("mss")
public class MethodSecurityService {

    private PermissionService permissionService;
    private ProjectDAO projectRepository;
    private BranchDAO branchRepository;
    private OrgDAO orgRepository;

    @Autowired
    public void setPermissionService(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    @Autowired
    public void setOrgRepository(OrgDAO orgRepository) {
        this.orgRepository = orgRepository;
    }

    @Autowired
    public void setProjectRepository(ProjectDAO projectRepository) {
        this.projectRepository = projectRepository;
    }

    @Autowired
    public void setBranchRepository(BranchDAO branchRepository) {
        this.branchRepository = branchRepository;
    }


    public boolean hasOrgPrivilege(Authentication authentication, String orgId, String privilege, boolean allowAnonIfPublic) {
        CompletableFuture<Boolean> permissionsFuture = CompletableFuture.supplyAsync(() ->
        {
            if (allowAnonIfPublic && permissionService.isOrgPublic(orgId)) {
                return true;
            }
            if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
                return false;
            }
            if (permissionService.hasOrgPrivilege(privilege, authentication.getName(), AuthenticationUtils.getGroups(authentication), orgId)) {
                return true;
            }
            return false;
        });

        CompletableFuture<Boolean> existsFuture = CompletableFuture.supplyAsync(() -> orgExists(orgId));

        if(!permissionsFuture.join()) {
            return false;
        }
        if(!existsFuture.join()) {
            throw new NotFoundException("Org not found");
        }
        return true;
    }

    public boolean hasProjectPrivilege(Authentication authentication, String projectId, String privilege, boolean allowAnonIfPublic) {
        CompletableFuture<Boolean> permissionsFuture = CompletableFuture.supplyAsync(() ->
        {
            if (allowAnonIfPublic && permissionService.isProjectPublic(projectId)) {
                return true;
            }
            if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
                return false;
            }
            if (permissionService.hasProjectPrivilege(privilege, authentication.getName(), AuthenticationUtils.getGroups(authentication), projectId)) {
                return true;
            }
            return false;
        });

        CompletableFuture<Boolean> existsFuture = CompletableFuture.supplyAsync(() -> projectExists(projectId));

        if(!permissionsFuture.join()) {
            return false;
        }
        if(!existsFuture.join()) {
            throw new NotFoundException("Project not found");
        }
        return true;
    }

    public boolean hasBranchPrivilege(Authentication authentication, String projectId, String branchId, String privilege, boolean allowAnonIfPublic) {
        CompletableFuture<Boolean> permissionsFuture = CompletableFuture.supplyAsync(() ->
        {
            if (allowAnonIfPublic && permissionService.isProjectPublic(projectId)) {
                return true;
            }
            if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
                return false;
            }
            if (permissionService.hasBranchPrivilege(privilege, authentication.getName(), AuthenticationUtils.getGroups(authentication), projectId, branchId)) {
                return true;
            }
            return false;
        });

        CompletableFuture<Boolean> existsFuture = CompletableFuture.supplyAsync(() -> branchExists(projectId, branchId));

        if(!permissionsFuture.join()) {
            return false;
        }
        if(!existsFuture.join()) {
            throw new NotFoundException("Org not found");
        }
        return true;
    }

    private boolean orgExists(String orgId) {
        Optional<Organization> o = orgRepository.findByOrganizationId(orgId);
        return o.isPresent();
    }

    private boolean projectExists(String projectId) {
        ContextHolder.getContext().setProjectId(projectId);
        Optional<Project> p = projectRepository.findByProjectId(projectId);
        return p.isPresent();
    }

    private boolean branchExists(String projectId, String branchId){
        if(! projectExists(projectId)) {
            return false;
        }
        Optional<Branch> branchesOption = branchRepository.findByBranchId(branchId);
        return branchesOption.isPresent();
    }
}
