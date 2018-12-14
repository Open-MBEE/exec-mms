package org.openmbee.sdvc.crud.controllers.elements;

import java.util.List;
import java.util.Map;
import org.openmbee.sdvc.crud.controllers.BaseController;
import org.openmbee.sdvc.crud.controllers.BaseResponse;
import org.openmbee.sdvc.crud.services.NodeService;
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
        @RequestParam Map<String, String> params) {

        NodeService nodeService = getNodeService(projectId);
        ElementsResponse res = nodeService.read(projectId, refId, elementId, params);
        return ResponseEntity.ok(res);
    }

    @PostMapping
    public ResponseEntity<? extends BaseResponse> handlePost(
        @PathVariable String projectId,
        @PathVariable String refId,
        @RequestBody ElementsRequest req,
        @RequestParam Map<String, String> params) {

        if (!req.getElements().isEmpty()) {
            NodeService nodeService = getNodeService(projectId);
            ElementsResponse response = nodeService.create(projectId, refId, req, params);
            return ResponseEntity.ok(response);
        }
        ElementsResponse err = new ElementsResponse();
        err.setCode(400);
        err.addMessage("Empty");
        return ResponseEntity.badRequest().body(err);
    }

    @PutMapping
    public ResponseEntity<? extends BaseResponse> handlePut(
        @PathVariable String projectId,
        @PathVariable String refId,
        @RequestBody ElementsRequest req,
        @RequestParam Map<String, String> params) {

        NodeService nodeService = getNodeService(projectId);
        ElementsResponse err = new ElementsResponse();
        return ResponseEntity.badRequest().body(err);
    }

    @DeleteMapping(value = "/{elementId}")
    @SuppressWarnings("unchecked")
    public ResponseEntity<? extends BaseResponse> handleDelete(
        @PathVariable String projectId,
        @PathVariable String refId,
        @PathVariable String elementId) {

        ElementsResponse res = getNodeService(projectId).delete(projectId, refId, elementId);
        if (res.getElements().isEmpty() && !res.isEmpty()) {
            List<Map<String, Object>> rejected = (List<Map<String, Object>>) res.get("rejected");
            Integer code = (Integer) rejected.get(1).get("code");
            if (code == 304) {
                return ResponseEntity.status(304).body(res);
            }
            if (code == 404) {
                return ResponseEntity.notFound().build();
            }
        }
        return ResponseEntity.ok(res);
    }

    @DeleteMapping
    public ResponseEntity<? extends BaseResponse> handleBulkDelete(
        @PathVariable String projectId,
        @PathVariable String refId,
        @RequestBody ElementsRequest req) {

        ElementsResponse res = getNodeService(projectId).delete(projectId, refId, req);
        return ResponseEntity.ok(res);
    }

    private NodeService getNodeService(String projectId) {
        return serviceFactory.getNodeService("cameo");
    }
}
