package org.openmbee.sdvc.twc.permissions;

import exceptions.TwcConfigurationException;
import org.openmbee.sdvc.core.delegation.PermissionsDelegate;
import org.openmbee.sdvc.core.delegation.PermissionsDelegateFactory;
import org.openmbee.sdvc.data.domains.global.Branch;
import org.openmbee.sdvc.data.domains.global.Organization;
import org.openmbee.sdvc.data.domains.global.Project;
import org.openmbee.sdvc.data.domains.global.TWCIntegration;
import org.openmbee.sdvc.rdb.repositories.TWCIntegrationRepository;
import org.openmbee.sdvc.twc.TeamworkCloud;
import org.openmbee.sdvc.twc.config.TwcConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;

import java.util.Optional;

public class TwcPermissionsDelegateFactory implements PermissionsDelegateFactory {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private TwcConfig twcConfig;

    @Autowired
    private TWCIntegrationRepository twcIntegrationRepository;

    @Override
    public PermissionsDelegate getPermissionsDelegate(Project project) {

        TwcProjectDetails twcProjectDetails = getTWCDetails(project);
        if(twcProjectDetails != null) {
            return autowire(new TwcProjectPermissionsDelegate(project, twcProjectDetails.getTeamworkCloud(),
                twcProjectDetails.getWorkspaceId(), twcProjectDetails.getResourceId()));
        }

        return null;
    }

    @Override
    public PermissionsDelegate getPermissionsDelegate(Organization organization) {
        //TODO implement this once category-level permissions are implemented in TWC
        return null;
    }

    @Override
    public PermissionsDelegate getPermissionsDelegate(Branch branch) {
        TwcProjectDetails twcProjectDetails = getTWCDetails(branch.getProject());
        if(twcProjectDetails != null) {
            return autowire(new TwcBranchPermissionsDelegate(branch, twcProjectDetails.getTeamworkCloud(),
                twcProjectDetails.getWorkspaceId(), twcProjectDetails.getResourceId()));
        }

        return null;
    }

    private PermissionsDelegate autowire(PermissionsDelegate permissionsDelegate) {
        applicationContext.getAutowireCapableBeanFactory().autowireBean(permissionsDelegate);
        return permissionsDelegate;
    }

    private class TwcProjectDetails {

        private TeamworkCloud teamworkCloud;
        private String workspaceId;
        private String resourceId;

        public TeamworkCloud getTeamworkCloud() {
            return teamworkCloud;
        }

        public void setTeamworkCloud(TeamworkCloud teamworkCloud) {
            this.teamworkCloud = teamworkCloud;
        }

        public String getWorkspaceId() {
            return workspaceId;
        }

        public void setWorkspaceId(String workspaceId) {
            this.workspaceId = workspaceId;
        }

        public String getResourceId() {
            return resourceId;
        }

        public void setResourceId(String resourceId) {
            this.resourceId = resourceId;
        }
    }

    private TwcProjectDetails getTWCDetails(Project project) {
        Optional<TWCIntegration> twcIntegration = twcIntegrationRepository.findTWCIntegrationByProjectId(project.getProjectId());

        if(! twcIntegration.isPresent()) {
            return null;
        }

        TWCIntegration ti = twcIntegration.get();
        TeamworkCloud teamworkCloud = twcConfig.getTeamworkCloud(ti.getUrl());

        if(teamworkCloud == null) {
            throw new TwcConfigurationException(HttpStatus.FAILED_DEPENDENCY,
                "Project " + project.getProjectId() + " (" + project.getProjectName()
                    + ") is associated with an untrusted TWC host (" + ti.getUrl() + ")");
        }

        TwcProjectDetails twcProjectDetails = new TwcProjectDetails();
        twcProjectDetails.setTeamworkCloud(teamworkCloud);
        twcProjectDetails.setWorkspaceId(ti.getWorkspaceId());
        twcProjectDetails.setResourceId(ti.getResourceId());

        return twcProjectDetails;
    }
}
