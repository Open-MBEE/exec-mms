package org.openmbee.sdvc.jupyter.controllers;

import javax.transaction.Transactional;
import org.openmbee.sdvc.core.config.Privileges;
import org.openmbee.sdvc.crud.controllers.BaseController;
import org.openmbee.sdvc.core.objects.ElementsResponse;
import org.openmbee.sdvc.jupyter.services.JupyterNodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
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
    @Transactional
    public ResponseEntity<?> handleGet(
        @PathVariable String projectId,
        @PathVariable String refId,
        @PathVariable(required = false) String notebookId,
        @RequestParam(required = false) Map<String, String> params,
        Authentication auth) {

        if (!permissionService.isProjectPublic(projectId)) {
            rejectAnonymous(auth);
            checkBranchPrivilege(Privileges.BRANCH_READ.name(), "No permission to read branch", auth, projectId, refId);
        }
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
    @Transactional
    public ResponseEntity<?> handlePut(
        @PathVariable String projectId,
        @PathVariable String refId,
        @RequestBody NotebooksRequest req,
        @RequestParam(required = false) Map<String, String> params,
        Authentication auth) {

        if (!permissionService.isProjectPublic(projectId)) {
            rejectAnonymous(auth);
            checkBranchPrivilege(Privileges.BRANCH_READ.name(), "No permission to read branch", auth, projectId, refId);
        }
        ElementsResponse res = nodeService.readNotebooks(projectId, refId, req, params);
        NotebooksResponse resn = new NotebooksResponse();
        resn.setNotebooks(res.getElements());
        resn.setRejected(res.getRejected());
        return ResponseEntity.ok(resn);
    }

    @PostMapping
    @Transactional
    public ResponseEntity<?> handlePost(
        @PathVariable String projectId,
        @PathVariable String refId,
        @RequestBody NotebooksRequest req,
        @RequestParam(required = false) Map<String, String> params,
        Authentication auth) {

        rejectAnonymous(auth);
        checkBranchPrivilege(Privileges.BRANCH_EDIT_CONTENT.name(), "No permission to edit branch", auth, projectId, refId);
        return ResponseEntity.ok(nodeService.createOrUpdateNotebooks(projectId, refId, req, params, auth.getName()));
    }
}
