package org.openmbee.sdvc.jupyter.controllers;

import org.openmbee.sdvc.crud.controllers.BaseController;
import org.openmbee.sdvc.crud.controllers.elements.ElementsResponse;
import org.openmbee.sdvc.jupyter.services.JupyterNodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/projects/{projectId}/refs/{refId}/notebooks")
public class NotebooksController extends BaseController {

    private JupyterNodeService nodeService;

    @Autowired
    public NotebooksController(JupyterNodeService nodeService) {
        this.nodeService = nodeService;
    }

    @GetMapping(value = {"", "/{notebookId}"})
    public ResponseEntity<?> handleGet(
        @PathVariable String projectId,
        @PathVariable String refId,
        @PathVariable(required = false) String notebookId,
        @RequestParam Map<String, String> params) {

        ElementsResponse res = nodeService.readNotebooks(projectId, refId, notebookId, params);
        if (notebookId != null) {
            this.handleSingleResponse(res);
        }
        NotebooksResponse resn = new NotebooksResponse();
        resn.setNotebooks(res.getElements());
        resn.setRejected(res.getRejected());
        return ResponseEntity.ok(resn);
    }

    @PutMapping
    public ResponseEntity<?> handlePut(
        @PathVariable String projectId,
        @PathVariable String refId,
        @RequestBody NotebooksRequest req,
        @RequestParam Map<String, String> params) {

        ElementsResponse res = nodeService.readNotebooks(projectId, refId, req, params);
        NotebooksResponse resn = new NotebooksResponse();
        resn.setNotebooks(res.getElements());
        resn.setRejected(res.getRejected());
        return ResponseEntity.ok(resn);
    }

    @PostMapping
    public ResponseEntity<?> handlePost(
        @PathVariable String projectId,
        @PathVariable String refId,
        @RequestBody NotebooksRequest req,
        @RequestParam Map<String, String> params) {

        return ResponseEntity.ok(nodeService.createOrUpdateNotebooks(projectId, refId, req, params));
    }
}
