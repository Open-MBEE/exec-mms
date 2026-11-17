package org.openmbee.mms.crud.services;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.openmbee.mms.crud.CrudConstants;
import org.openmbee.mms.core.dao.CommitPersistence;
import org.openmbee.mms.core.services.NodeChangeInfo;
import org.openmbee.mms.core.dao.NodePersistence;
import org.openmbee.mms.core.objects.ElementsCommitResponse;
import org.openmbee.mms.core.objects.ElementsRequest;
import org.openmbee.mms.core.objects.EventObject;
import org.openmbee.mms.core.services.EventService;
import org.openmbee.mms.json.CommitJson;
import org.openmbee.mms.json.ElementJson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class DefaultNodeServiceTest {

    private static final String PROJECT_ID = "PROJECT_ID";
    private static final String REF_ID = "REF_ID";

    private NodePersistence nodePersistence;
    private EventService eventService;
    private NodeChangeInfo changes;
    private CommitJson commitJson;
    private Map<String, ElementJson> updatedMap;
    private Map<String, ElementJson> deletedMap;

    @Before
    public void setUp() {
        nodePersistence = mock(NodePersistence.class);
        eventService = mock(EventService.class);
        changes = mock(NodeChangeInfo.class);
        commitJson = new CommitJson();
        commitJson.setId("COMMIT_ID");
        commitJson.setProjectId(PROJECT_ID);
        commitJson.setRefId(REF_ID);
        updatedMap = new LinkedHashMap<>();
        deletedMap = new LinkedHashMap<>();

        when(changes.getCommitJson()).thenReturn(commitJson);
        when(changes.getUpdatedMap()).thenReturn(updatedMap);
        when(changes.getDeletedMap()).thenReturn(deletedMap);
        when(changes.getRejected()).thenReturn(Collections.emptyMap());
        when(nodePersistence.prepareChange(any(), anyBoolean(), anyBoolean())).thenReturn(changes);
        when(nodePersistence.prepareAddsUpdates(any(), any())).thenReturn(changes);
        when(nodePersistence.prepareDeletes(any(), any())).thenReturn(changes);
        when(nodePersistence.commitChanges(any())).thenReturn(changes);
    }

    private <T extends DefaultNodeService> T wire(T service) {
        service.setNodePersistence(nodePersistence);
        service.setCommitPersistence(mock(CommitPersistence.class));
        service.setEventPublisher(List.of(eventService));
        return service;
    }

    private static ElementsRequest request(String... ids) {
        List<ElementJson> elements = new ArrayList<>();
        for (String id : ids) {
            elements.add(new ElementJson().setId(id));
        }
        ElementsRequest req = new ElementsRequest();
        req.setElements(elements);
        return req;
    }

    @Test
    public void createOrUpdateAllowsElementsAddedWhileProcessing() {
        updatedMap.put("a", new ElementJson().setId("a"));
        updatedMap.put("b", new ElementJson().setId("b"));
        // Stands in for the view services, which add generated properties and associations.
        DefaultNodeService service = wire(new DefaultNodeService() {
            @Override
            public void extraProcessPostedElement(NodeChangeInfo info, ElementJson element) {
                String generatedId = element.getId() + "-generated";
                info.getUpdatedMap().put(generatedId, new ElementJson().setId(generatedId));
                super.extraProcessPostedElement(info, element);
            }
        });

        ElementsCommitResponse response = service.createOrUpdate(PROJECT_ID, REF_ID, request("a", "b"),
            Collections.emptyMap(), "user");

        assertEquals(4, response.getElements().size());
        assertEquals(List.of("a", "b", "a-generated", "b-generated"), List.copyOf(updatedMap.keySet()));
    }

    @Test
    public void deletePublishesCommitEvent() {
        deletedMap.put("a", new ElementJson().setId("a"));
        DefaultNodeService service = wire(new DefaultNodeService());

        ElementsCommitResponse response = service.delete(PROJECT_ID, REF_ID, request("a"), "user");

        assertEquals("COMMIT_ID", response.getCommitId());
        ArgumentCaptor<EventObject> event = ArgumentCaptor.forClass(EventObject.class);
        verify(eventService).publish(event.capture());
        assertEquals(PROJECT_ID, event.getValue().getProjectId());
        assertEquals(REF_ID, event.getValue().getBranchId());
        assertEquals(CrudConstants.COMMIT_SC, event.getValue().getEvent());
        assertSame(commitJson, event.getValue().getPayload());
    }

    @Test
    public void deleteOfNothingPublishesNoEvent() {
        DefaultNodeService service = wire(new DefaultNodeService());

        service.delete(PROJECT_ID, REF_ID, request("missing"), "user");

        verify(eventService, never()).publish(any());
    }
}
