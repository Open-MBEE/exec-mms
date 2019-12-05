package org.openmbee.sdvc.webhooks.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.openmbee.sdvc.core.dao.ProjectDAO;
import org.openmbee.sdvc.core.dao.WebhookDAO;
import org.openmbee.sdvc.core.exceptions.BadRequestException;
import org.openmbee.sdvc.core.exceptions.NotFoundException;
import org.openmbee.sdvc.core.objects.BaseResponse;
import org.openmbee.sdvc.data.domains.global.Project;
import org.openmbee.sdvc.data.domains.global.Webhook;
import org.openmbee.sdvc.webhooks.json.WebhookJson;
import org.openmbee.sdvc.webhooks.objects.WebhookRequest;
import org.openmbee.sdvc.webhooks.objects.WebhookResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/projects/{projectId}/webhooks")
public class WebhooksController {

    private WebhookDAO webhookRepository;
    private ProjectDAO projectRepository;
    private ObjectMapper om;

    @Autowired
    public void setWebhookRepository(WebhookDAO webhookRepository) {
        this.webhookRepository = webhookRepository;
    }

    @Autowired
    public void setProjectRepository(ProjectDAO projectRepository) {
        this.projectRepository = projectRepository;
    }

    @Autowired
    public void setObjectMapper(ObjectMapper om) {
        this.om = om;
    }

    @GetMapping
    @PreAuthorize("@mss.hasProjectPrivilege(authentication, #projectId, 'PROJECT_READ', true)")
    public WebhookResponse getAllWebhooks(@PathVariable String projectId) {

        WebhookResponse response = new WebhookResponse();
        List<Webhook> webhooks = webhookRepository.findAllByProject_ProjectId(projectId);

        for (Webhook webhook : webhooks) {
            WebhookJson webhookJson = new WebhookJson();
            webhookJson.merge(convertToMap(webhook));
            webhookJson.setId(webhook.getId().toString());
            response.getWebhooks().add(webhookJson);
        }
        return response;
    }

    @PostMapping
    @Transactional
    @PreAuthorize("@mss.hasProjectPrivilege(authentication, #projectId, 'PROJECT_CREATE_WEBHOOKS', false)")
    public WebhookResponse createOrUpdateWebhooks(@PathVariable String projectId, @RequestBody WebhookRequest webhooksPost) {

        if (webhooksPost.getWebhooks().isEmpty()) {
            throw new BadRequestException(new WebhookResponse().addMessage("No web hooks provided"));
        }

        WebhookResponse response = new WebhookResponse();
        Optional<Project> project = projectRepository.findByProjectId(projectId);

        for (WebhookJson json: webhooksPost.getWebhooks()) {
            Optional<Webhook> existing = webhookExists(json, projectId);

            if (!existing.isPresent()) {
                Webhook newWebhook = new Webhook();
                newWebhook.setProject(project.get());
                newWebhook.setUrl(json.getUrl());
                webhookRepository.save(newWebhook);
                json.setId(newWebhook.getId().toString());
                response.getWebhooks().add(json);
            } else {
                Webhook existingHook = existing.get();
                existingHook.setUrl(json.getUrl());
                webhookRepository.save(existingHook);
                json.setId(existingHook.getId().toString());
                response.getWebhooks().add(json);
            }
        }
        return response;
    }

    @DeleteMapping
    @Transactional
    @PreAuthorize("@mss.hasProjectPrivilege(authentication, #projectId, 'PROJECT_CREATE_WEBHOOKS', false)")
    public WebhookResponse deleteWebhooks(@PathVariable String projectId, @RequestBody WebhookRequest webhookRequest) {

        // TODO: Determine which webhook to delete somehow. Maybe require webhook id.

        Set<String> uris = new HashSet<>();
        for (WebhookJson webhookJson : webhookRequest.getWebhooks()) {
            uris.add(webhookJson.getUrl());
        }

        WebhookResponse response = new WebhookResponse();
        List<Webhook> webhooks = webhookRepository.findAllByProject_ProjectId(projectId);
        if (webhooks.isEmpty()) {
            throw new NotFoundException(response.addMessage("No web hooks found for project"));
        }
        for (Webhook webhook : webhooks) {
            if (uris.contains(webhook.getUrl())) {
                webhookRepository.delete(webhook);
                response.addMessage(String.format("Web hook for project %s to %s deleted", projectId, webhook.getUrl()));
            }
        }
        return response;
    }

    public Map<String, Object> convertToMap(Object obj) {
        return om.convertValue(obj, new TypeReference<Map<String, Object>>() {});
    }

    private Optional<Webhook> webhookExists(WebhookJson json, String projectId) {
        if (json.getId() != null) {
            Optional<Webhook> hook = webhookRepository.findById(Long.parseLong(json.getId()));
            if (hook.isPresent() && hook.get().getProject().getProjectId().equals(projectId)) {
                return hook; //ensure hook by id matches the project being requested
            }
        }
        return webhookRepository.findByProject_ProjectIdAndUrl(projectId, json.getUrl());
    }
}
