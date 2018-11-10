package org.openmbee.sdvc.crud.controllers.elements;

import java.util.Map;

import org.openmbee.sdvc.crud.controllers.BaseController;
import org.openmbee.sdvc.crud.controllers.ErrorResponse;
import org.openmbee.sdvc.crud.services.NodeService;
import org.openmbee.sdvc.crud.services.NodeServiceFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/projects/{projectId}/refs/{refId}/elements")
public class ElementsController extends BaseController {

    private NodeServiceFactory nodeServiceFactory;

    @Autowired
    public ElementsController(NodeServiceFactory nodeServiceFactory) {
        this.nodeServiceFactory = nodeServiceFactory;
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

    private NodeService getNodeService(String projectId) {
        return nodeServiceFactory.getNodeService("sysml");
    }
}
