package org.openmbee.sdvc.crud.controllers.elements;

import java.util.Map;

import javax.transaction.Transactional;
import org.openmbee.sdvc.core.objects.ElementsRequest;
import org.openmbee.sdvc.core.objects.ElementsResponse;
import org.openmbee.sdvc.crud.controllers.BaseController;
import org.openmbee.sdvc.core.objects.BaseResponse;
import org.openmbee.sdvc.crud.exceptions.BadRequestException;
import org.openmbee.sdvc.core.services.NodeService;
import org.springframework.http.ResponseEntity;
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
@Transactional
public class ElementsController extends BaseController {

    @GetMapping(value = {"", "/{elementId}"})
    @PreAuthorize("hasBranchPrivilege(#projectId, #refId, 'BRANCH_READ', true)")
    public ResponseEntity<? extends BaseResponse> handleGet(
        @PathVariable String projectId,
        @PathVariable String refId,
        @PathVariable(required = false) String elementId,
        @RequestParam(required = false) Map<String, String> params) {

        NodeService nodeService = getNodeService(projectId);
        ElementsResponse res = nodeService.read(projectId, refId, elementId, params);
        if (elementId != null) {
            handleSingleResponse(res);
        }
        return ResponseEntity.ok(res);
    }

    @PostMapping
    @PreAuthorize("hasBranchPrivilege(#projectId, #refId, 'BRANCH_EDIT_CONTENT', false)")
    public ResponseEntity<? extends BaseResponse> handlePost(
        @PathVariable String projectId,
        @PathVariable String refId,
        @RequestBody ElementsRequest req,
        @RequestParam(required = false) Map<String, String> params,
        Authentication auth) {

        ElementsResponse response = new ElementsResponse();
        if (!req.getElements().isEmpty()) {
            NodeService nodeService = getNodeService(projectId);
            response = nodeService.createOrUpdate(projectId, refId, req, params, auth.getName());
            return ResponseEntity.ok(response);
        }
        throw new BadRequestException(response.addMessage("Empty"));
    }

    @PutMapping
    @PreAuthorize("hasBranchPrivilege(#projectId, #refId, 'BRANCH_READ', true)")
    public ResponseEntity<? extends BaseResponse> handlePut(
        @PathVariable String projectId,
        @PathVariable String refId,
        @RequestBody ElementsRequest req,
        @RequestParam(required = false) Map<String, String> params) {

        ElementsResponse response = new ElementsResponse();
        if (!req.getElements().isEmpty()) {
            NodeService nodeService = getNodeService(projectId);
            response = nodeService.read(projectId, refId, req, params);
            return ResponseEntity.ok(response);
        }
        throw new BadRequestException(response.addMessage("Empty"));
    }

    @DeleteMapping(value = "/{elementId}")
    @PreAuthorize("hasBranchPrivilege(#projectId, #refId, 'BRANCH_EDIT_CONTENT', false)")
    public ResponseEntity<? extends BaseResponse> handleDelete(
        @PathVariable String projectId,
        @PathVariable String refId,
        @PathVariable String elementId,
        Authentication auth) {

        ElementsResponse res = getNodeService(projectId).delete(projectId, refId, elementId, auth.getName());
        handleSingleResponse(res);
        return ResponseEntity.ok(res);
    }

    @DeleteMapping
    @PreAuthorize("hasBranchPrivilege(#projectId, #refId, 'BRANCH_DELETE', false)")
    public ResponseEntity<? extends BaseResponse> handleBulkDelete(
        @PathVariable String projectId,
        @PathVariable String refId,
        @RequestBody ElementsRequest req,
        Authentication auth) {

        ElementsResponse res = getNodeService(projectId).delete(projectId, refId, req, auth.getName());
        return ResponseEntity.ok(res);
    }
}
