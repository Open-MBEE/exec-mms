package org.openmbee.mms.webhooks.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.openmbee.mms.core.dao.ProjectDAO;
import org.openmbee.mms.core.dao.WebhookDAO;
import org.openmbee.mms.core.exceptions.BadRequestException;
import org.openmbee.mms.core.exceptions.NotFoundException;
import org.openmbee.mms.data.domains.global.Project;
import org.openmbee.mms.data.domains.global.Webhook;
import org.openmbee.mms.webhooks.json.WebhookJson;
import org.openmbee.mms.webhooks.objects.WebhookRequest;
import org.openmbee.mms.webhooks.objects.WebhookResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/projects/{projectId}/webhooks")
@Tag(name = "Webhooks")
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
            webhookJson.setProjectId(projectId);
            response.getWebhooks().add(webhookJson);
        }
        return response;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
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
            Webhook hook;
            if (!existing.isPresent()) {
                hook = new Webhook();
                hook.setProject(project.get());
            } else {
                hook = existing.get();
            }
            hook.setUrl(json.getUrl());
            webhookRepository.save(hook);
            json.setId(hook.getId().toString());
            json.setProjectId(projectId);
            response.getWebhooks().add(json);
        }
        return response;
    }

    @DeleteMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    @PreAuthorize("@mss.hasProjectPrivilege(authentication, #projectId, 'PROJECT_CREATE_WEBHOOKS', false)")
    public WebhookResponse deleteWebhooks(@PathVariable String projectId, @RequestBody WebhookRequest webhookRequest) {

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
