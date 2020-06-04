package org.openmbee.sdvc.crud.controllers.elements;

import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.Map;

import org.openmbee.sdvc.core.objects.ElementsRequest;
import org.openmbee.sdvc.core.objects.ElementsResponse;
import org.openmbee.sdvc.crud.controllers.BaseController;
import org.openmbee.sdvc.core.exceptions.BadRequestException;
import org.openmbee.sdvc.core.services.NodeService;
import org.openmbee.sdvc.core.pubsub.EmbeddedHookService;
import org.openmbee.sdvc.crud.hooks.ElementUpdateHook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/projects/{projectId}/refs/{refId}/elements")
@Tag(name = "Elements")
public class ElementsController extends BaseController {

    private EmbeddedHookService embeddedHookService;

    @Autowired
    public void setEmbeddedHookService(EmbeddedHookService embeddedHookService) {
        this.embeddedHookService = embeddedHookService;
    }

    @GetMapping
    @PreAuthorize("@mss.hasBranchPrivilege(authentication, #projectId, #refId, 'BRANCH_READ', true)")
    public ElementsResponse getAllElements(
        @PathVariable String projectId,
        @PathVariable String refId,
        @RequestParam(required = false) String commitId,
        @RequestParam(required = false) Map<String, String> params) {

        NodeService nodeService = getNodeService(projectId);
        ElementsResponse res = nodeService.read(projectId, refId, "", params);
        return res;
    }

    @GetMapping(value = "/{elementId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("@mss.hasBranchPrivilege(authentication, #projectId, #refId, 'BRANCH_READ', true)")
    public ElementsResponse getElement(
        @PathVariable String projectId,
        @PathVariable String refId,
        @PathVariable String elementId,
        @RequestParam(required = false) String commitId,
        @RequestParam(required = false) Map<String, String> params) {

        NodeService nodeService = getNodeService(projectId);
        ElementsResponse res = nodeService.read(projectId, refId, elementId, params);
        handleSingleResponse(res);
        return res;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("@mss.hasBranchPrivilege(authentication, #projectId, #refId, 'BRANCH_EDIT_CONTENT', false)")
    public ElementsResponse createOrUpdateElements(
        @PathVariable String projectId,
        @PathVariable String refId,
        @RequestBody ElementsRequest req,
        @RequestParam(required = false) Map<String, String> params,
        Authentication auth) {

        embeddedHookService.hook(new ElementUpdateHook(ElementUpdateHook.Action.ADD_UPDATE, projectId, refId,
            req.getElements(), params, auth));

        ElementsResponse response = new ElementsResponse();
        if (!req.getElements().isEmpty()) {
            NodeService nodeService = getNodeService(projectId);
            return nodeService.createOrUpdate(projectId, refId, req, params, auth.getName());
        }
        throw new BadRequestException(response.addMessage("Empty"));
    }

    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("@mss.hasBranchPrivilege(authentication, #projectId, #refId, 'BRANCH_READ', true)")
    public ElementsResponse getElements(
        @PathVariable String projectId,
        @PathVariable String refId,
        @RequestBody ElementsRequest req,
        @RequestParam(required = false) String commitId,
        @RequestParam(required = false) Map<String, String> params) {

        ElementsResponse response = new ElementsResponse();
        if (!req.getElements().isEmpty()) {
            NodeService nodeService = getNodeService(projectId);
            return nodeService.read(projectId, refId, req, params);
        }
        throw new BadRequestException(response.addMessage("Empty"));
    }

    @DeleteMapping(value = "/{elementId}")
    @PreAuthorize("@mss.hasBranchPrivilege(authentication, #projectId, #refId, 'BRANCH_EDIT_CONTENT', false)")
    public ElementsResponse deleteElement(
        @PathVariable String projectId,
        @PathVariable String refId,
        @PathVariable String elementId,
        Authentication auth) {

        ElementsResponse res = getNodeService(projectId).delete(projectId, refId, elementId, auth.getName());
        handleSingleResponse(res);
        return res;
    }

    @DeleteMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("@mss.hasBranchPrivilege(authentication, #projectId, #refId, 'BRANCH_EDIT_CONTENT', false)")
    public ElementsResponse deleteElements(
        @PathVariable String projectId,
        @PathVariable String refId,
        @RequestBody ElementsRequest req,
        Authentication auth) {

        return getNodeService(projectId).delete(projectId, refId, req, auth.getName());
    }
}
