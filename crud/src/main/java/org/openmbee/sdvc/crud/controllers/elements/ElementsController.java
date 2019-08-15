package org.openmbee.sdvc.crud.controllers.elements;

import java.util.Map;

import org.openmbee.sdvc.core.objects.ElementsRequest;
import org.openmbee.sdvc.core.objects.ElementsResponse;
import org.openmbee.sdvc.crud.controllers.BaseController;
import org.openmbee.sdvc.core.objects.BaseResponse;
import org.openmbee.sdvc.crud.exceptions.BadRequestException;
import org.openmbee.sdvc.core.services.NodeService;
import org.springframework.http.ResponseEntity;
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
public class ElementsController extends BaseController {

    @GetMapping(value = {"", "/{elementId}"})
    public ResponseEntity<? extends BaseResponse> handleGet(
        @PathVariable String projectId,
        @PathVariable String refId,
        @PathVariable(required = false) String elementId,
        @RequestParam(required = false) Map<String, String> params) {

        NodeService nodeService = getNodeService(projectId);
        ElementsResponse res = (ElementsResponse) nodeService.read(projectId, refId, elementId, params);
        if (elementId != null) {
            handleSingleResponse(res);
        }
        return ResponseEntity.ok(res);
    }

    @PostMapping
    public ResponseEntity<? extends BaseResponse> handlePost(
        @PathVariable String projectId,
        @PathVariable String refId,
        @RequestBody ElementsRequest req,
        @RequestParam(required = false) Map<String, String> params) {

        ElementsResponse response = new ElementsResponse();
        if (!req.getElements().isEmpty()) {
            NodeService nodeService = getNodeService(projectId);
            response = (ElementsResponse) nodeService.createOrUpdate(projectId, refId, req, params);
            return ResponseEntity.ok(response);
        }
        throw new BadRequestException(response.addMessage("Empty"));
    }

    @PutMapping
    public ResponseEntity<? extends BaseResponse> handlePut(
        @PathVariable String projectId,
        @PathVariable String refId,
        @RequestBody ElementsRequest req,
        @RequestParam(required = false) Map<String, String> params) {

        ElementsResponse response = new ElementsResponse();
        if (!req.getElements().isEmpty()) {
            NodeService nodeService = getNodeService(projectId);
            response = (ElementsResponse) nodeService.read(projectId, refId, req, params);
            return ResponseEntity.ok(response);
        }
        throw new BadRequestException(response.addMessage("Empty"));
    }

    @DeleteMapping(value = "/{elementId}")
    @SuppressWarnings("unchecked")
    public ResponseEntity<? extends BaseResponse> handleDelete(
        @PathVariable String projectId,
        @PathVariable String refId,
        @PathVariable String elementId) {

        ElementsResponse res = (ElementsResponse) getNodeService(projectId).delete(projectId, refId, elementId);
        handleSingleResponse(res);
        return ResponseEntity.ok(res);
    }

    @DeleteMapping
    public ResponseEntity<? extends BaseResponse> handleBulkDelete(
        @PathVariable String projectId,
        @PathVariable String refId,
        @RequestBody ElementsRequest req) {

        ElementsResponse res = (ElementsResponse) getNodeService(projectId).delete(projectId, refId, req);
        return ResponseEntity.ok(res);
    }
}
