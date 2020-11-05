package org.openmbee.mms.twc.metadata;

public class TwcMetadataServiceTest {
// TODO: update for implementation change

//    @Test
//    public void testUpdateExistingMetadata() {
//        Project project = new Project();
//        Metadata host = new Metadata();
//        host.setKey(HOST_KEY);
//        host.setValue("host1");
//        Metadata workspace = new Metadata();
//        workspace.setKey(WORKSPACE_ID_KEY);
//        workspace.setValue("workspace1");
//        Metadata resource = new Metadata();
//        resource.setKey(RESOURCE_ID_KEY);
//        resource.setValue("resource1");
//
//        project.setMetadata(List.of(host, workspace, resource));
//
//        TwcMetadataService twcMetadataService = new TwcMetadataService();
//
//        MetadataRepository metadataRepository = mock(MetadataRepository.class);
//        twcMetadataService.setMetadataRepository(metadataRepository);
//
//        TwcMetadata newMetadata = new TwcMetadata();
//        newMetadata.setHost("host2");
//        newMetadata.setWorkspaceId("workspace2");
//        newMetadata.setResourceId("resource2");
//
//        when(metadataRepository.save(any())).thenAnswer(v -> {
//            Metadata metadata = v.getArgument(0);
//            assertTrue(metadata.getValue().endsWith("2"));
//            return metadata;
//        });
//
//        twcMetadataService.updateTwcMetadata(project, newMetadata);
//
//        verify(metadataRepository, times(3)).save(any());
//        verify(metadataRepository, times(0)).delete(any());
//        verify(metadataRepository, times(0)).deleteAll(any());
//    }
//
//    @Test
//    public void testAddMetadata() {
//        Project project = new Project();
//        project.setMetadata(Collections.emptyList());
//
//        TwcMetadataService twcMetadataService = new TwcMetadataService();
//
//        MetadataRepository metadataRepository = mock(MetadataRepository.class);
//        twcMetadataService.setMetadataRepository(metadataRepository);
//
//        TwcMetadata newMetadata = new TwcMetadata();
//        newMetadata.setHost("host2");
//        newMetadata.setWorkspaceId("workspace2");
//        newMetadata.setResourceId("resource2");
//
//        when(metadataRepository.save(any())).thenAnswer(v -> {
//            Metadata metadata = v.getArgument(0);
//            assertTrue(metadata.getValue().endsWith("2"));
//            return metadata;
//        });
//
//        twcMetadataService.updateTwcMetadata(project, newMetadata);
//
//        verify(metadataRepository, times(3)).save(any());
//        verify(metadataRepository, times(0)).delete(any());
//        verify(metadataRepository, times(0)).deleteAll(any());
//    }
//
//
//    @Test
//    public void testRemoveExistingMetadata() {
//        Project project = new Project();
//        Metadata host = new Metadata();
//        host.setKey(HOST_KEY);
//        host.setValue("host1");
//        Metadata workspace = new Metadata();
//        workspace.setKey(WORKSPACE_ID_KEY);
//        workspace.setValue("workspace1");
//        Metadata resource = new Metadata();
//        resource.setKey(RESOURCE_ID_KEY);
//        resource.setValue("resource1");
//
//        project.setMetadata(List.of(host, workspace, resource));
//
//        TwcMetadataService twcMetadataService = new TwcMetadataService();
//
//        MetadataRepository metadataRepository = mock(MetadataRepository.class);
//        twcMetadataService.setMetadataRepository(metadataRepository);
//
//        TwcMetadata newMetadata = new TwcMetadata();
//
//        twcMetadataService.updateTwcMetadata(project, newMetadata);
//
//        verify(metadataRepository, times(0)).save(any());
//        verify(metadataRepository, times(3)).delete(any());
//        verify(metadataRepository, times(0)).deleteAll(any());
//    }
//
//    @Test
//    public void testRemoveNothing() {
//        Project project = new Project();
//        project.setMetadata(Collections.emptyList());
//
//        TwcMetadataService twcMetadataService = new TwcMetadataService();
//
//        MetadataRepository metadataRepository = mock(MetadataRepository.class);
//        twcMetadataService.setMetadataRepository(metadataRepository);
//
//        TwcMetadata newMetadata = new TwcMetadata();
//
//        twcMetadataService.updateTwcMetadata(project, newMetadata);
//
//        verify(metadataRepository, times(0)).save(any());
//        verify(metadataRepository, times(0)).delete(any());
//        verify(metadataRepository, times(0)).deleteAll(any());
//    }
//
//    @Test
//    public void testUpdateExistingMetadataDeletedExtras() {
//        Project project = new Project();
//        Metadata host1 = new Metadata();
//        host1.setKey(HOST_KEY);
//        host1.setValue("host1");
//        Metadata host2 = new Metadata();
//        host2.setKey(HOST_KEY);
//        host2.setValue("hostx");
//        Metadata workspace = new Metadata();
//        workspace.setKey(WORKSPACE_ID_KEY);
//        workspace.setValue("workspace1");
//        Metadata resource = new Metadata();
//        resource.setKey(RESOURCE_ID_KEY);
//        resource.setValue("resource1");
//
//        project.setMetadata(List.of(host1, host2, workspace, resource));
//
//        TwcMetadataService twcMetadataService = new TwcMetadataService();
//
//        MetadataRepository metadataRepository = mock(MetadataRepository.class);
//        twcMetadataService.setMetadataRepository(metadataRepository);
//
//        TwcMetadata newMetadata = new TwcMetadata();
//        newMetadata.setHost("host2");
//        newMetadata.setWorkspaceId("workspace2");
//        newMetadata.setResourceId("resource2");
//
//        when(metadataRepository.save(any())).thenAnswer(v -> {
//            Metadata metadata = v.getArgument(0);
//            assertTrue(metadata.getValue().endsWith("2"));
//            return metadata;
//        });
//
//        twcMetadataService.updateTwcMetadata(project, newMetadata);
//
//        verify(metadataRepository, times(3)).save(any());
//        verify(metadataRepository, times(0)).delete(any());
//        verify(metadataRepository, times(1)).deleteAll(any());
//    }
//
//    @Test
//    public void testGetTwcMetadata() {
//        Project project = new Project();
//        Metadata host = new Metadata();
//        host.setKey(HOST_KEY);
//        host.setValue("host1");
//        Metadata workspace = new Metadata();
//        workspace.setKey(WORKSPACE_ID_KEY);
//        workspace.setValue("workspace1");
//        Metadata resource = new Metadata();
//        resource.setKey(RESOURCE_ID_KEY);
//        resource.setValue("resource1");
//
//        project.setMetadata(List.of(host, workspace, resource));
//
//        TwcMetadataService twcMetadataService = new TwcMetadataService();
//
//        MetadataRepository metadataRepository = mock(MetadataRepository.class);
//        twcMetadataService.setMetadataRepository(metadataRepository);
//
//        TwcMetadata twcMetadata = twcMetadataService.getTwcMetadata(project);
//
//        assertEquals("host1", twcMetadata.getHost());
//        assertEquals("workspace1", twcMetadata.getWorkspaceId());
//        assertEquals("resource1", twcMetadata.getResourceId());
//
//        verify(metadataRepository, times(0)).save(any());
//        verify(metadataRepository, times(0)).delete(any());
//        verify(metadataRepository, times(0)).deleteAll(any());
//    }
//
//    @Test
//    public void testGetTwcMetadataEmptyList() {
//        Project project = new Project();
//
//        project.setMetadata(Collections.emptyList());
//
//        TwcMetadataService twcMetadataService = new TwcMetadataService();
//
//        MetadataRepository metadataRepository = mock(MetadataRepository.class);
//        twcMetadataService.setMetadataRepository(metadataRepository);
//
//        TwcMetadata twcMetadata = twcMetadataService.getTwcMetadata(project);
//
//        assertNull(twcMetadata.getHost());
//        assertNull(twcMetadata.getWorkspaceId());
//        assertNull(twcMetadata.getResourceId());
//
//        verify(metadataRepository, times(0)).save(any());
//        verify(metadataRepository, times(0)).delete(any());
//        verify(metadataRepository, times(0)).deleteAll(any());
//    }
//
//    @Test
//    public void testGetTwcMetadataNullList() {
//        Project project = new Project();
//
//        TwcMetadataService twcMetadataService = new TwcMetadataService();
//
//        MetadataRepository metadataRepository = mock(MetadataRepository.class);
//        twcMetadataService.setMetadataRepository(metadataRepository);
//
//        TwcMetadata twcMetadata = twcMetadataService.getTwcMetadata(project);
//
//        assertNull(twcMetadata.getHost());
//        assertNull(twcMetadata.getWorkspaceId());
//        assertNull(twcMetadata.getResourceId());
//
//        verify(metadataRepository, times(0)).save(any());
//        verify(metadataRepository, times(0)).delete(any());
//        verify(metadataRepository, times(0)).deleteAll(any());
//    }

}