package org.openmbee.mms.twc.permissions;

import org.junit.Test;
import org.openmbee.mms.core.delegation.PermissionsDelegate;
import org.openmbee.mms.data.domains.global.Branch;
import org.openmbee.mms.data.domains.global.Organization;
import org.openmbee.mms.data.domains.global.Project;
import org.openmbee.mms.twc.TeamworkCloud;
import org.openmbee.mms.twc.config.TwcConfig;
import org.openmbee.mms.twc.exceptions.TwcConfigurationException;
import org.openmbee.mms.twc.metadata.TwcMetadata;
import org.openmbee.mms.twc.metadata.TwcMetadataService;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class TwcPermissionsDelegateFactoryTest {


    @Test
    public void testTwcOrg() {
        ApplicationContext applicationContext = mock(ApplicationContext.class);
        TwcConfig twcConfig = mock(TwcConfig.class);
        TwcMetadataService twcMetadataService = mock(TwcMetadataService.class);

        when(twcConfig.isUseAuthDelegation()).thenReturn(true);

        TwcPermissionsDelegateFactory twcPermissionsDelegateFactory = new TwcPermissionsDelegateFactory();
        twcPermissionsDelegateFactory.setApplicationContext(applicationContext);
        twcPermissionsDelegateFactory.setTwcConfig(twcConfig);
        twcPermissionsDelegateFactory.setTwcMetadataService(twcMetadataService);

        //These are not supported
        assertNull( twcPermissionsDelegateFactory.getPermissionsDelegate(new Organization()));
    }

    @Test
    public void testTwcProject() {

        ApplicationContext applicationContext = mock(ApplicationContext.class);
        TwcConfig twcConfig = mock(TwcConfig.class);
        TwcMetadataService twcMetadataService = mock(TwcMetadataService.class);

        when(twcConfig.isUseAuthDelegation()).thenReturn(true);

        TwcPermissionsDelegateFactory twcPermissionsDelegateFactory = new TwcPermissionsDelegateFactory();
        twcPermissionsDelegateFactory.setApplicationContext(applicationContext);
        twcPermissionsDelegateFactory.setTwcConfig(twcConfig);
        twcPermissionsDelegateFactory.setTwcMetadataService(twcMetadataService);

        Project project = new Project();
        TwcMetadata twcMetadata = new TwcMetadata();
        twcMetadata.setHost("host");
        twcMetadata.setWorkspaceId("workspace");
        twcMetadata.setResourceId("resource");

        when(twcMetadataService.getTwcMetadata(project)).thenReturn(twcMetadata);

        TeamworkCloud teamworkCloud = new TeamworkCloud();
        when(twcConfig.getTeamworkCloud("host")).thenReturn(teamworkCloud);

        AutowireCapableBeanFactory autowireCapableBeanFactory  = mock(AutowireCapableBeanFactory.class);
        when(applicationContext.getAutowireCapableBeanFactory()).thenReturn(autowireCapableBeanFactory);

        PermissionsDelegate delegate = twcPermissionsDelegateFactory.getPermissionsDelegate(project);

        verify(autowireCapableBeanFactory, times(1)).autowireBean(any());

        assertTrue(delegate instanceof TwcProjectPermissionsDelegate);

        TwcProjectPermissionsDelegate twcProjectPermissionsDelegate = (TwcProjectPermissionsDelegate) delegate;
        assertSame(project, twcProjectPermissionsDelegate.getProject());
        assertSame(teamworkCloud, twcProjectPermissionsDelegate.getTeamworkCloud());
        assertEquals("workspace", twcProjectPermissionsDelegate.getWorkspaceId());
        assertEquals("resource", twcProjectPermissionsDelegate.getResourceId());
    }

    @Test
    public void testTwcProjectDoesNotMatchTwc() {
        ApplicationContext applicationContext = mock(ApplicationContext.class);
        TwcConfig twcConfig = mock(TwcConfig.class);
        TwcMetadataService twcMetadataService = mock(TwcMetadataService.class);

        when(twcConfig.isUseAuthDelegation()).thenReturn(true);

        TwcPermissionsDelegateFactory twcPermissionsDelegateFactory = new TwcPermissionsDelegateFactory();
        twcPermissionsDelegateFactory.setApplicationContext(applicationContext);
        twcPermissionsDelegateFactory.setTwcConfig(twcConfig);
        twcPermissionsDelegateFactory.setTwcMetadataService(twcMetadataService);

        Project project = new Project();
        TwcMetadata twcMetadata = new TwcMetadata();
        twcMetadata.setHost("host");
        twcMetadata.setWorkspaceId("workspace");
        twcMetadata.setResourceId("resource");

        when(twcMetadataService.getTwcMetadata(project)).thenReturn(twcMetadata);

        when(twcConfig.getTeamworkCloud("host")).thenReturn(null);

        try {
            PermissionsDelegate delegate = twcPermissionsDelegateFactory.getPermissionsDelegate(project);
            fail("Should have thrown TwcConfigurationException");
        } catch(TwcConfigurationException ex){}
    }

    @Test
    public void testTwcProjectIncompleteMetadata() {
        ApplicationContext applicationContext = mock(ApplicationContext.class);
        TwcConfig twcConfig = mock(TwcConfig.class);
        TwcMetadataService twcMetadataService = mock(TwcMetadataService.class);

        TwcPermissionsDelegateFactory twcPermissionsDelegateFactory = new TwcPermissionsDelegateFactory();
        twcPermissionsDelegateFactory.setApplicationContext(applicationContext);
        twcPermissionsDelegateFactory.setTwcConfig(twcConfig);
        twcPermissionsDelegateFactory.setTwcMetadataService(twcMetadataService);

        Project project = new Project();
        TwcMetadata twcMetadata = new TwcMetadata();
        twcMetadata.setHost("host");
        twcMetadata.setWorkspaceId("workspace");
        //twcMetadata.setResourceId("resource"); //Resource is missing

        when(twcMetadataService.getTwcMetadata(project)).thenReturn(twcMetadata);

        PermissionsDelegate delegate = twcPermissionsDelegateFactory.getPermissionsDelegate(project);
        assertNull(delegate);
        verify(twcConfig, times(0)).getTeamworkCloud(any());
    }

    @Test
    public void testTwcProjectNullMetadata() {
        ApplicationContext applicationContext = mock(ApplicationContext.class);
        TwcConfig twcConfig = mock(TwcConfig.class);
        TwcMetadataService twcMetadataService = mock(TwcMetadataService.class);

        TwcPermissionsDelegateFactory twcPermissionsDelegateFactory = new TwcPermissionsDelegateFactory();
        twcPermissionsDelegateFactory.setApplicationContext(applicationContext);
        twcPermissionsDelegateFactory.setTwcConfig(twcConfig);
        twcPermissionsDelegateFactory.setTwcMetadataService(twcMetadataService);

        Project project = new Project();

        when(twcMetadataService.getTwcMetadata(project)).thenReturn(null);

        PermissionsDelegate delegate = twcPermissionsDelegateFactory.getPermissionsDelegate(project);
        assertNull(delegate);
        verify(twcConfig, times(0)).getTeamworkCloud(any());
    }


    @Test
    public void testTwcBranch() {

        ApplicationContext applicationContext = mock(ApplicationContext.class);
        TwcConfig twcConfig = mock(TwcConfig.class);
        TwcMetadataService twcMetadataService = mock(TwcMetadataService.class);

        when(twcConfig.isUseAuthDelegation()).thenReturn(true);

        TwcPermissionsDelegateFactory twcPermissionsDelegateFactory = new TwcPermissionsDelegateFactory();
        twcPermissionsDelegateFactory.setApplicationContext(applicationContext);
        twcPermissionsDelegateFactory.setTwcConfig(twcConfig);
        twcPermissionsDelegateFactory.setTwcMetadataService(twcMetadataService);

        Project project = new Project();
        Branch branch = new Branch();
        branch.setProject(project);

        TwcMetadata twcMetadata = new TwcMetadata();
        twcMetadata.setHost("host");
        twcMetadata.setWorkspaceId("workspace");
        twcMetadata.setResourceId("resource");

        when(twcMetadataService.getTwcMetadata(project)).thenReturn(twcMetadata);

        TeamworkCloud teamworkCloud = new TeamworkCloud();
        when(twcConfig.getTeamworkCloud("host")).thenReturn(teamworkCloud);

        AutowireCapableBeanFactory autowireCapableBeanFactory  = mock(AutowireCapableBeanFactory.class);
        when(applicationContext.getAutowireCapableBeanFactory()).thenReturn(autowireCapableBeanFactory);

        PermissionsDelegate delegate = twcPermissionsDelegateFactory.getPermissionsDelegate(branch);

        verify(autowireCapableBeanFactory, times(1)).autowireBean(any());

        assertTrue(delegate instanceof TwcBranchPermissionsDelegate);

        TwcBranchPermissionsDelegate twcBranchPermissionsDelegate = (TwcBranchPermissionsDelegate) delegate;
        assertSame(branch, twcBranchPermissionsDelegate.getBranch());
        assertSame(teamworkCloud, twcBranchPermissionsDelegate.getTeamworkCloud());
        assertEquals("workspace", twcBranchPermissionsDelegate.getWorkspaceId());
        assertEquals("resource", twcBranchPermissionsDelegate.getResourceId());
    }

    @Test
    public void testTwcBranchDoesNotMatchTwc() {
        ApplicationContext applicationContext = mock(ApplicationContext.class);
        TwcConfig twcConfig = mock(TwcConfig.class);
        TwcMetadataService twcMetadataService = mock(TwcMetadataService.class);

        when(twcConfig.isUseAuthDelegation()).thenReturn(true);

        TwcPermissionsDelegateFactory twcPermissionsDelegateFactory = new TwcPermissionsDelegateFactory();
        twcPermissionsDelegateFactory.setApplicationContext(applicationContext);
        twcPermissionsDelegateFactory.setTwcConfig(twcConfig);
        twcPermissionsDelegateFactory.setTwcMetadataService(twcMetadataService);

        Project project = new Project();
        Branch branch = new Branch();
        branch.setProject(project);

        TwcMetadata twcMetadata = new TwcMetadata();
        twcMetadata.setHost("host");
        twcMetadata.setWorkspaceId("workspace");
        twcMetadata.setResourceId("resource");

        when(twcMetadataService.getTwcMetadata(project)).thenReturn(twcMetadata);

        when(twcConfig.getTeamworkCloud("host")).thenReturn(null);

        try {
            PermissionsDelegate delegate = twcPermissionsDelegateFactory.getPermissionsDelegate(branch);
            fail("Should have thrown TwcConfigurationException");
        } catch(TwcConfigurationException ex){}
    }

    @Test
    public void testTwcBranchIncompleteMetadata() {
        ApplicationContext applicationContext = mock(ApplicationContext.class);
        TwcConfig twcConfig = mock(TwcConfig.class);
        TwcMetadataService twcMetadataService = mock(TwcMetadataService.class);

        TwcPermissionsDelegateFactory twcPermissionsDelegateFactory = new TwcPermissionsDelegateFactory();
        twcPermissionsDelegateFactory.setApplicationContext(applicationContext);
        twcPermissionsDelegateFactory.setTwcConfig(twcConfig);
        twcPermissionsDelegateFactory.setTwcMetadataService(twcMetadataService);

        Project project = new Project();
        Branch branch = new Branch();
        branch.setProject(project);

        TwcMetadata twcMetadata = new TwcMetadata();
        twcMetadata.setHost("host");
        twcMetadata.setWorkspaceId("workspace");
        //twcMetadata.setResourceId("resource"); //Resource is missing

        when(twcMetadataService.getTwcMetadata(project)).thenReturn(twcMetadata);

        PermissionsDelegate delegate = twcPermissionsDelegateFactory.getPermissionsDelegate(branch);
        assertNull(delegate);
        verify(twcConfig, times(0)).getTeamworkCloud(any());
    }

    @Test
    public void testTwcBranchNullMetadata() {
        ApplicationContext applicationContext = mock(ApplicationContext.class);
        TwcConfig twcConfig = mock(TwcConfig.class);
        TwcMetadataService twcMetadataService = mock(TwcMetadataService.class);

        TwcPermissionsDelegateFactory twcPermissionsDelegateFactory = new TwcPermissionsDelegateFactory();
        twcPermissionsDelegateFactory.setApplicationContext(applicationContext);
        twcPermissionsDelegateFactory.setTwcConfig(twcConfig);
        twcPermissionsDelegateFactory.setTwcMetadataService(twcMetadataService);

        Project project = new Project();
        Branch branch = new Branch();
        branch.setProject(project);

        when(twcMetadataService.getTwcMetadata(project)).thenReturn(null);

        PermissionsDelegate delegate = twcPermissionsDelegateFactory.getPermissionsDelegate(branch);
        assertNull(delegate);
        verify(twcConfig, times(0)).getTeamworkCloud(any());
    }
}