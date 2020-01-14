package org.openmbee.sdvc.jupyter.controllers;

import io.swagger.v3.oas.annotations.Parameter;
import org.openmbee.sdvc.crud.controllers.BaseController;
import org.openmbee.sdvc.core.objects.ElementsResponse;
import org.openmbee.sdvc.jupyter.services.JupyterNodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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

    @GetMapping
    @PreAuthorize("@mss.hasBranchPrivilege(authentication, #projectId, #refId, 'BRANCH_READ', true)")
    public NotebooksResponse getAllNotebooks(
        @PathVariable String projectId,
        @PathVariable String refId,
        @RequestParam(required = false) String commitId,
        @RequestParam(required = false) Map<String, String> params) {

        ElementsResponse res = nodeService.readNotebooks(projectId, refId, "", params);
        NotebooksResponse resn = new NotebooksResponse();
        resn.setNotebooks(res.getElements());
        resn.setRejected(res.getRejected());
        return resn;
    }

    @GetMapping(value = "/{notebookId}")
    @PreAuthorize("@mss.hasBranchPrivilege(authentication, #projectId, #refId, 'BRANCH_READ', true)")
    public NotebooksResponse getNotebook(
        @PathVariable String projectId,
        @PathVariable String refId,
        @PathVariable String notebookId,
        @RequestParam(required = false) String commitId,
        @RequestParam(required = false) Map<String, String> params) {

        ElementsResponse res = nodeService.readNotebooks(projectId, refId, notebookId, params);
        this.handleSingleResponse(res);
        NotebooksResponse resn = new NotebooksResponse();
        resn.setNotebooks(res.getElements());
        resn.setRejected(res.getRejected());
        return resn;
    }

    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("@mss.hasBranchPrivilege(authentication, #projectId, #refId, 'BRANCH_READ', true)")
    public NotebooksResponse getNotebooks(
        @PathVariable String projectId,
        @PathVariable String refId,
        @RequestBody NotebooksRequest req,
        @RequestParam(required = false) String commitId,
        @RequestParam(required = false) Map<String, String> params) {

        ElementsResponse res = nodeService.readNotebooks(projectId, refId, req, params);
        NotebooksResponse resn = new NotebooksResponse();
        resn.setNotebooks(res.getElements());
        resn.setRejected(res.getRejected());
        return resn;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("@mss.hasBranchPrivilege(authentication, #projectId, #refId, 'BRANCH_EDIT_CONTENT', false)")
    public NotebooksResponse createOrUpdateNotebooks(
        @PathVariable String projectId,
        @PathVariable String refId,
        @RequestBody NotebooksRequest req,
        @RequestParam(required = false) Map<String, String> params,
        @Parameter(hidden = true) Authentication auth) {

        return nodeService.createOrUpdateNotebooks(projectId, refId, req, params, auth.getName());
    }
}
