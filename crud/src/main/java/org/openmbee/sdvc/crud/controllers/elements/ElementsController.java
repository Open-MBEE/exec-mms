package org.openmbee.sdvc.crud.controllers.elements;

import java.util.Map;
import org.openmbee.sdvc.crud.controllers.BaseController;
import org.openmbee.sdvc.crud.controllers.ErrorResponse;
import org.openmbee.sdvc.crud.services.NodeService;
import org.openmbee.sdvc.crud.services.ServiceFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

    private ServiceFactory serviceFactory;

    @Autowired
    public ElementsController(ServiceFactory serviceFactory) {
        this.serviceFactory = serviceFactory;
    }

    @GetMapping(value = {"", "/{elementId}"})
    public ResponseEntity<?> handleGet(
        @PathVariable String projectId,
        @PathVariable String refId,
        @PathVariable(required = false) String elementId,
        @RequestParam Map<String, String> params) {

        NodeService nodeService = getNodeService(projectId);
        ElementsResponse res = nodeService.get(projectId, refId, elementId, params);
        return ResponseEntity.ok(res);
    }

    @PostMapping
    public ResponseEntity<?> handlePost(
        @PathVariable String projectId,
        @PathVariable String refId,
        @RequestBody ElementsRequest req,
        @RequestParam Map<String, String> params) {

        if (!req.getElements().isEmpty()) {
            NodeService nodeService = getNodeService(projectId);
            ElementsResponse response = nodeService.post(projectId, refId, req, params);
            return ResponseEntity.ok(response);
        }
        logger.debug("Bad Request");
        ErrorResponse err = new ErrorResponse();
        err.setCode(400);
        err.setError("Bad Request");
        return ResponseEntity.badRequest().body(err);
    }

    @PutMapping
    public ResponseEntity<?> handlePut(
        @PathVariable String projectId,
        @PathVariable String refId,
        @RequestBody ElementsRequest req,
        @RequestParam Map<String, String> params) {

        NodeService nodeService = getNodeService(projectId);
        ErrorResponse err = new ErrorResponse();
        return ResponseEntity.badRequest().body(err);
    }

    @DeleteMapping(value = "/{elementId}")
    public ResponseEntity<?> handleDelete(
        @PathVariable String projectId,
        @PathVariable String refId,
        @PathVariable String elementId) {

        return ResponseEntity.ok(new ElementsResponse());
    }

    @DeleteMapping
    public ResponseEntity<?> handleBulkDelete(
        @PathVariable String projectId,
        @PathVariable String refId,
        @RequestBody ElementsRequest req) {

        return ResponseEntity.ok(new ElementsResponse());
    }

    private NodeService getNodeService(String projectId) {
        return serviceFactory.getNodeService("sysml");
    }
}
