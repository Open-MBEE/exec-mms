package org.openmbee.mms.federatedpersistence.dao;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.openmbee.mms.core.exceptions.InternalErrorException;
import org.openmbee.mms.data.dao.ProjectDAO;
import org.openmbee.mms.data.dao.WebhookDAO;
import org.openmbee.mms.data.domains.global.Project;
import org.openmbee.mms.data.domains.global.Webhook;
import org.openmbee.mms.federatedpersistence.utils.FederatedJsonUtils;
import org.openmbee.mms.webhooks.json.WebhookJson;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class FederatedWebhookPersistenceTest {

    private static final String VICTIM_URL = "http://victim.example/hook";

    private WebhookDAO webhookDao;
    private FederatedWebhookPersistence persistence;
    private Webhook victimWebhook;

    @Before
    public void setUp() {
        webhookDao = mock(WebhookDAO.class);
        ProjectDAO projectDao = mock(ProjectDAO.class);
        FederatedJsonUtils jsonUtils = new FederatedJsonUtils();
        jsonUtils.setObjectMapper(new ObjectMapper());

        persistence = new FederatedWebhookPersistence();
        persistence.setWebhookDao(webhookDao);
        persistence.setProjectDao(projectDao);
        persistence.setJsonUtils(jsonUtils);

        Project victim = project("victim");
        Project other = project("other");
        when(projectDao.findByProjectId("victim")).thenReturn(Optional.of(victim));
        when(projectDao.findByProjectId("other")).thenReturn(Optional.of(other));

        victimWebhook = new Webhook(victim, VICTIM_URL);
        victimWebhook.setId(1L);
        when(webhookDao.findById(1L)).thenReturn(Optional.of(victimWebhook));
        when(webhookDao.findByProject_ProjectIdAndUrl("victim", VICTIM_URL)).thenReturn(Optional.of(victimWebhook));
        when(webhookDao.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
    }

    private static Project project(String projectId) {
        Project project = new Project();
        project.setProjectId(projectId);
        return project;
    }

    private static WebhookJson webhookJson(String id, String projectId, String url) {
        WebhookJson json = new WebhookJson();
        json.setId(id);
        json.setProjectId(projectId);
        json.setUrl(url);
        return json;
    }

    @Test
    public void saveRejectsIdOfAnotherProjectsWebhook() {
        WebhookJson posted = webhookJson("1", "other", "http://attacker.example/hook");

        assertThrows(InternalErrorException.class, () -> persistence.save(posted));

        verify(webhookDao, never()).save(any());
        assertEquals(VICTIM_URL, victimWebhook.getUrl());
        assertEquals("victim", victimWebhook.getProject().getProjectId());
    }

    @Test
    public void saveUpdatesWebhookOfSameProjectById() {
        WebhookJson saved = persistence.save(webhookJson("1", "victim", "http://victim.example/moved"));

        verify(webhookDao).save(victimWebhook);
        assertEquals("http://victim.example/moved", victimWebhook.getUrl());
        assertEquals("1", saved.getId());
    }

    @Test
    public void lookupsReturnTheOwningProject() {
        assertEquals("victim", persistence.findById("1").orElseThrow().getProjectId());
        assertEquals("victim", persistence.findByProjectIdAndUrl("victim", VICTIM_URL).orElseThrow().getProjectId());
    }
}
