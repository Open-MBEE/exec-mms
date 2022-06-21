package org.openmbee.mms.twc.permissions;

import org.openmbee.mms.core.delegation.PermissionsDelegate;
import org.openmbee.mms.core.delegation.PermissionsDelegateFactory;
import org.openmbee.mms.data.domains.global.Group;
import org.openmbee.mms.twc.TeamworkCloud;
import org.openmbee.mms.core.exceptions.NotFoundException;
import org.openmbee.mms.data.domains.global.Branch;
import org.openmbee.mms.data.domains.global.Organization;
import org.openmbee.mms.data.domains.global.Project;
import org.openmbee.mms.twc.config.TwcConfig;
import org.openmbee.mms.twc.exceptions.TwcConfigurationException;
import org.openmbee.mms.twc.metadata.TwcMetadata;
import org.openmbee.mms.twc.metadata.TwcMetadataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;


public class TwcPermissionsDelegateFactory implements PermissionsDelegateFactory {

    private ApplicationContext applicationContext;
    private TwcConfig twcConfig;
    private TwcMetadataService twcMetadataService;


    @Autowired
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Autowired
    public void setTwcConfig(TwcConfig twcConfig) {
        this.twcConfig = twcConfig;
    }

    @Override
    public PermissionsDelegate getPermissionsDelegate(Project project) {
        if(!twcConfig.isUseAuthDelegation()) {
            return null;
        }

        TwcProjectDetails twcProjectDetails = getTwcDetails(project);
        if(twcProjectDetails != null) {
            return autowire(new TwcProjectPermissionsDelegate(project, twcProjectDetails.getTeamworkCloud(),
                twcProjectDetails.getWorkspaceId(), twcProjectDetails.getResourceId()));
        }

        return null;
    }

    @Override
    public PermissionsDelegate getPermissionsDelegate(Organization organization) {
        if(!twcConfig.isUseAuthDelegation()) {
            return null;
        }

        //TODO implement this once category-level permissions are implemented in TWC
        return null;
    }

    @Override
    public PermissionsDelegate getPermissionsDelegate(Branch branch) {
        if(!twcConfig.isUseAuthDelegation()) {
            return null;
        }

        TwcProjectDetails twcProjectDetails = getTwcDetails(branch.getProject());
        if(twcProjectDetails != null) {
            return autowire(new TwcBranchPermissionsDelegate(branch, twcProjectDetails.getTeamworkCloud(),
                twcProjectDetails.getWorkspaceId(), twcProjectDetails.getResourceId()));
        }

        return null;
    }

    @Override
    public PermissionsDelegate getPermissionsDelegate(Group group) {
        if(!twcConfig.isUseAuthDelegation()) {
            return null;
        }

            return autowire(new TwcGroupPermissionsDelegate(group));

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

    private TwcProjectDetails getTwcDetails(Project project) {
        TwcMetadata twcMetadata = null;
        try {
            twcMetadata = twcMetadataService.getTwcMetadata(project);
        }
        catch (NotFoundException e){ }

        if(twcMetadata == null || ! twcMetadata.isComplete()) {
            return null;
        }

        TeamworkCloud teamworkCloud = twcConfig.getTeamworkCloud(twcMetadata.getHost());

        if(teamworkCloud == null) {
            throw new TwcConfigurationException(HttpStatus.FAILED_DEPENDENCY,
                "Project " + project.getProjectId() + " (" + project.getProjectName()
                    + ") is associated with an untrusted TWC host (" + twcMetadata.getHost() + ")");
        }

        TwcProjectDetails twcProjectDetails = new TwcProjectDetails();
        twcProjectDetails.setTeamworkCloud(teamworkCloud);
        twcProjectDetails.setWorkspaceId(twcMetadata.getWorkspaceId());
        twcProjectDetails.setResourceId(twcMetadata.getResourceId());

        return twcProjectDetails;
    }
}
