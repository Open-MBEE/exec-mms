package org.openmbee.sdvc.webhooks.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.openmbee.sdvc.core.config.Privileges;
import org.openmbee.sdvc.core.dao.ProjectDAO;
import org.openmbee.sdvc.core.dao.WebhookDAO;
import org.openmbee.sdvc.core.exceptions.BadRequestException;
import org.openmbee.sdvc.core.exceptions.NotFoundException;
import org.openmbee.sdvc.core.objects.BaseResponse;
import org.openmbee.sdvc.core.security.MethodSecurityService;
import org.openmbee.sdvc.data.domains.global.Project;
import org.openmbee.sdvc.data.domains.global.Webhook;
import org.openmbee.sdvc.webhooks.json.WebhookJson;
import org.openmbee.sdvc.webhooks.objects.WebhookRequest;
import org.openmbee.sdvc.webhooks.objects.WebhookResponse;
import org.openmbee.sdvc.webhooks.services.WebhookEventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/webhooks")
public class WebhooksController {

    private WebhookDAO webhookRepository;
    private ProjectDAO projectRepository;
    private WebhookEventService webhookEventService;
    private MethodSecurityService mss;
    private ObjectMapper om;

    @Autowired
    public WebhooksController(WebhookDAO webhookRepository, WebhookEventService webhookEventService) {
        this.webhookRepository = webhookRepository;
        this.webhookEventService = webhookEventService;
    }

    @Autowired
    public void setWebhookRepository(WebhookDAO webhookRepository) {
        this.webhookRepository = webhookRepository;
    }

    @Autowired
    public void setProjectRepository(ProjectDAO projectRepository) {
        this.projectRepository = projectRepository;
    }

    @Autowired
    public void setWebhookEventService(WebhookEventService webhookEventService) {
        this.webhookEventService = webhookEventService;
    }

    @Autowired
    public void setMss(MethodSecurityService mss) {
        this.mss = mss;
    }

    @Autowired
    public void setObjectMapper(ObjectMapper om) {
        this.om = om;
    }

    @GetMapping(value = {"", "/{projectId}"})
    @PreAuthorize("#projectId == null || @mss.hasProjectPrivilege(authentication, #projectId, 'PROJECT_READ', true)")
    public ResponseEntity<? extends BaseResponse> handleGet(@PathVariable(required = false) String projectId, Authentication auth) {

        WebhookResponse response = new WebhookResponse();
        if (projectId != null) {
            List<Webhook> webhooks = webhookRepository.findAllByProject_ProjectId(projectId);
            if (webhooks.isEmpty()) {
                throw new NotFoundException(response.addMessage("No webhooks not found"));
            }
            WebhookJson webhookJson = new WebhookJson();
            for (Webhook webhook : webhooks) {
                webhookJson.merge(convertToMap(webhook));
            }
            response.getWebhooks().add(webhookJson);
        } else {
            List<Webhook> allWebhooks = webhookRepository.findAll();
            for (Webhook webhook : allWebhooks) {
                WebhookJson webhookJson = new WebhookJson();
                webhookJson.merge(convertToMap(webhook));
                response.getWebhooks().add(webhookJson);
            }
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping
    @Transactional
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<? extends BaseResponse> handlePost( @RequestBody WebhookRequest webhooksPost, Authentication auth) {

        if (webhooksPost.getWebhooks().isEmpty()) {
            throw new BadRequestException(new WebhookResponse().addMessage("No projects provided"));
        }

        WebhookResponse response = new WebhookResponse();
        List<Map> rejected = new ArrayList<>();
        response.setRejected(rejected);
        for (WebhookJson json: webhooksPost.getWebhooks()) {
            if (json.getProjectId().isEmpty()) {
                Map<String, Object> rejection = new HashMap<>();
                rejection.put("message", "Project id missing");
                rejection.put("code", 400);
                rejection.put("request", json);
                rejected.add(rejection);
                continue;
            }

            if (!webhookExists(json)) {
                try {
                    if (!mss.hasProjectPrivilege(auth, json.getProjectId(), Privileges.PROJECT_CREATE_WEBHOOKS.name(), false)) {
                        Map<String, Object> rejection = new HashMap<>();
                        rejection.put("message", "No permission to create project under org");
                        rejection.put("code", 403);
                        rejection.put("request", json);
                        rejected.add(rejection);
                        continue;
                    }

                    Optional<Project> project = projectRepository.findByProjectId(json.getProjectId());
                    project.ifPresentOrElse(proj -> {
                        Webhook newWebhook = new Webhook();
                        newWebhook.setProject(proj);
                        newWebhook.setUri(json.getUri());
                        webhookRepository.save(newWebhook);
                        response.getWebhooks().add(json);
                    }, () -> {
                        Map<String, Object> rejection = new HashMap<>();
                        rejection.put("message", "No project found");
                        rejection.put("code", 404);
                        rejection.put("request", json);
                        rejected.add(rejection);
                    });

                } catch (BadRequestException ex) {
                    Map<String, Object> rejection = new HashMap<>();
                    rejection.put("message", "Org to put project under is not found");
                    rejection.put("code", 400);
                    rejection.put("request", json);
                    rejected.add(rejection);
                    continue;
                }
            } else {
                Map<String, Object> rejection = new HashMap<>();
                rejection.put("message", "No permission to change project");
                rejection.put("code", 403);
                rejection.put("request", json);
                rejected.add(rejection);
                continue;
            }
        }
        return ResponseEntity.ok(response);
    }

    @DeleteMapping
    @Transactional
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<? extends BaseResponse> handleDelete(
        @RequestBody WebhookRequest webhookRequest,
        @RequestParam(required = false, defaultValue = "false") boolean hard) {

        // TODO: Determine which webhook to delete somehow. Maybe require webhook id.

        Set<String> projects = new HashSet<>();
        Set<String> uris = new HashSet<>();
        for (WebhookJson webhookJson : webhookRequest.getWebhooks()) {
            projects.add(webhookJson.getProjectId());
            uris.add(webhookJson.getUri());
        }

        WebhookResponse response = new WebhookResponse();
        for (String projectId : projects) {
            List<Webhook> webhooks = webhookRepository.findAllByProject_ProjectId(projectId);
            if (webhooks.isEmpty()) {
                throw new NotFoundException(response.addMessage("No webhooks found for project"));
            }
            for (Webhook webhook : webhooks) {
                if (uris.contains(webhook.getUri())) {
                    webhookRepository.delete(webhook);
                    response.addMessage(String.format("Webhook for project %s to %s deleted", webhook.getProject().getProjectId(), webhook.getUri()));
                }
            }
        }

        return ResponseEntity.ok(response);
    }

    public Map<String, Object> convertToMap(Object obj) {
        return om.convertValue(obj, new TypeReference<Map<String, Object>>() {});
    }

    private boolean webhookExists(WebhookJson json) {
        List<Webhook> projectWebhooks = webhookRepository.findAllByProject_ProjectId(json.getProjectId());
        for (Webhook webhook : projectWebhooks) {
            if (webhook.getUri() != null && webhook.getUri().equalsIgnoreCase(json.getUri())) {
                return true;
            }
        }
        return false;
    }
}
