package org.openmbee.sdvc.webhooks.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.openmbee.sdvc.core.config.Privileges;
import org.openmbee.sdvc.core.dao.WebhookDAO;
import org.openmbee.sdvc.core.exceptions.BadRequestException;
import org.openmbee.sdvc.core.exceptions.NotFoundException;
import org.openmbee.sdvc.core.objects.BaseResponse;
import org.openmbee.sdvc.core.security.MethodSecurityService;
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

public class WebhooksController {

    WebhookDAO webhookRepository;
    WebhookEventService webhookEventService;
    protected MethodSecurityService mss;

    protected ObjectMapper om;

    @Autowired
    public WebhooksController(WebhookDAO webhookRepository, WebhookEventService webhookEventService) {
        this.webhookRepository = webhookRepository;
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
                rejection.put("project", json);
                rejected.add(rejection);
                continue;
            }

            if (!ps.exists(json.getProjectId())) {
                try {
                    if (!mss.hasOrgPrivilege(auth, json.getProjectId(), Privileges.PROJECT_CREATE_WEBHOOKS.name(), false)) {
                        Map<String, Object> rejection = new HashMap<>();
                        rejection.put("message", "No permission to create project under org");
                        rejection.put("code", 403);
                        rejection.put("project", json);
                        rejected.add(rejection);
                        continue;
                    }
                    response.getWebhooks().add(ps.create(json));
                } catch (BadRequestException ex) {
                    Map<String, Object> rejection = new HashMap<>();
                    rejection.put("message", "Org to put project under is not found");
                    rejection.put("code", 400);
                    rejection.put("project", json);
                    rejected.add(rejection);
                    continue;
                }
            } else {
                if (!mss.hasProjectPrivilege(auth, json.getProjectId(), Privileges.PROJECT_EDIT.name(), false)) {
                    Map<String, Object> rejection = new HashMap<>();
                    rejection.put("message", "No permission to change project");
                    rejection.put("code", 403);
                    rejection.put("project", json);
                    rejected.add(rejection);
                    continue;
                }
                //TODO need to check delete perm on proj and create perm in new org if moving org, and reset project perms if org changed
                response.getWebhooks().add(ps.update(json));
            }
        }
        if (webhooksPost.getWebhooks().size() == 1) {
            handleSingleResponse(response);
        }
        return ResponseEntity.ok(response);
    }

    @DeleteMapping(value = "/{projectId}")
    @Transactional
    @PreAuthorize("@mss.hasProjectPrivilege(authentication, #projectId, 'PROJECT_DELETE', false)")
    public ResponseEntity<? extends BaseResponse> handleDelete(
        @PathVariable String projectId,
        @RequestParam(required = false, defaultValue = "false") boolean hard) {

        // TODO: Determine which webhook to delete somehow. Maybe require webhook id.

        WebhookResponse response = new WebhookResponse();
        List<Webhook> webhooks = webhookRepository.findAllByProject_ProjectId(projectId);
        if (webhooks.isEmpty()) {
            throw new NotFoundException(response.addMessage("Project not found"));
        }
        Webhook project = webhooks.get();
        project.setDeleted(true);
        WebhookJson projectJson = new WebhookJson();
        projectJson.merge(convertToMap(project));
        List<WebhookJson> res = new ArrayList<>();
        res.add(projectJson);
        if (hard) {
            webhookRepository.delete(project);
        } else {
            webhookRepository.save(project);
        }
        return ResponseEntity.ok(response.setProjects(res));
    }

    public Map<String, Object> convertToMap(Object obj) {
        return om.convertValue(obj, new TypeReference<Map<String, Object>>() {});
    }
}
