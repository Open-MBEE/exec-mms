package org.openmbee.mms.federatedpersistence.permissions;

import org.openmbee.mms.core.config.ContextHolder;
import org.openmbee.mms.data.dao.BranchGDAO;
import org.openmbee.mms.data.dao.OrgDAO;
import org.openmbee.mms.data.dao.ProjectDAO;
import org.openmbee.mms.core.delegation.PermissionsDelegate;
import org.openmbee.mms.core.delegation.PermissionsDelegateFactory;
import org.openmbee.mms.core.exceptions.NotFoundException;
import org.openmbee.mms.data.domains.global.Branch;
import org.openmbee.mms.data.domains.global.Organization;
import org.openmbee.mms.data.domains.global.Project;
import org.openmbee.mms.json.OrgJson;
import org.openmbee.mms.json.ProjectJson;
import org.openmbee.mms.json.RefJson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import java.util.Optional;

public class DefaultFederatedPermissionsDelegateFactory implements PermissionsDelegateFactory {

    private ApplicationContext applicationContext;
    private ProjectDAO projectDAO;
    private BranchGDAO branchDAO;
    private OrgDAO orgDAO;

    @Autowired
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Autowired
    public void setProjectDAO(ProjectDAO projectDAO) {
        this.projectDAO = projectDAO;
    }

    @Autowired
    public void setBranchDAO(BranchGDAO branchDAO) {
        this.branchDAO = branchDAO;
    }

    @Autowired
    public void setOrgDAO(OrgDAO orgDAO) {
        this.orgDAO = orgDAO;
    }

    @Override
    public PermissionsDelegate getPermissionsDelegate(ProjectJson project) {
        Optional<Project> projectOptional = projectDAO.findByProjectId(project.getProjectId());

        if(projectOptional.isEmpty()) {
            throw new NotFoundException("project not found");
        }

        return autowire(new DefaultProjectPermissionsDelegate(projectOptional.get()));
    }

    @Override
    public PermissionsDelegate getPermissionsDelegate(OrgJson organization) {
        Optional<Organization> orgOptional = orgDAO.findByOrganizationId(organization.getId());
        if(orgOptional.isEmpty()) {
            throw new NotFoundException("org not found");
        }
        return autowire(new DefaultOrgPermissionsDelegate(orgOptional.get()));
    }

    @Override
    public PermissionsDelegate getPermissionsDelegate(RefJson branch) {
        ContextHolder.setContext(null);
        Optional<Branch> branchOptional = branchDAO.findByProject_ProjectIdAndBranchId(branch.getProjectId(), branch.getId());
        if(branchOptional.isEmpty()) {
            throw new NotFoundException("branch not found");
        }
        return autowire(new DefaultBranchPermissionsDelegate(branchOptional.get()));
    }

    private PermissionsDelegate autowire(PermissionsDelegate permissionsDelegate) {
        applicationContext.getAutowireCapableBeanFactory().autowireBean(permissionsDelegate);
        return permissionsDelegate;
    }

}
