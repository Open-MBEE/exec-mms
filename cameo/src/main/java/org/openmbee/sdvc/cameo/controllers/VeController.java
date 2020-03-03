package org.openmbee.sdvc.cameo.controllers;

import java.util.ArrayList;
import java.util.Map;
import org.openmbee.sdvc.cameo.services.CameoViewService;
import org.openmbee.sdvc.core.objects.ElementsRequest;
import org.openmbee.sdvc.core.objects.ElementsResponse;
import org.openmbee.sdvc.crud.controllers.BaseController;
import org.openmbee.sdvc.json.MountJson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/projects/{projectId}/refs/{refId}")
public class VeController extends BaseController {

    private CameoViewService cameoViewService;

    @Autowired
    public VeController(CameoViewService cameoViewService) {
        this.cameoViewService = cameoViewService;
    }

    @GetMapping("/mounts")
    @PreAuthorize("@mss.hasBranchPrivilege(authentication, #projectId, #refId, 'BRANCH_READ', true)")
    public MountsResponse getMounts(
        @PathVariable String projectId,
        @PathVariable String refId,
        @RequestParam(required = false) Map<String, String> params) {

        MountJson json = cameoViewService
            .getProjectUsages(projectId, refId, params.get("commitId"), new ArrayList<>());
        MountsResponse res = new MountsResponse();
        res.getProjects().add(json);
        return res;
    }

    @GetMapping("/documents")
    @PreAuthorize("@mss.hasBranchPrivilege(authentication, #projectId, #refId, 'BRANCH_READ', true)")
    public DocumentsResponse getDocuments(
        @PathVariable String projectId,
        @PathVariable String refId,
        @RequestParam(required = false) Map<String, String> params) {

        ElementsResponse docs = cameoViewService.getDocuments(projectId, refId, params);
        return (new DocumentsResponse()).setDocuments(docs.getElements());
    }

    @GetMapping("/views/{viewId}")
    @PreAuthorize("@mss.hasBranchPrivilege(authentication, #projectId, #refId, 'BRANCH_READ', true)")
    public ElementsResponse getViews(
        @PathVariable String projectId,
        @PathVariable String refId,
        @PathVariable String viewId,
        @RequestParam(required = false) Map<String, String> params) {

        return cameoViewService.getView(projectId, refId, viewId, params);
    }

    @PutMapping("/views")
    @PreAuthorize("@mss.hasBranchPrivilege(authentication, #projectId, #refId, 'BRANCH_READ', true)")
    public ElementsResponse getViews(
        @PathVariable String projectId,
        @PathVariable String refId,
        @RequestBody ElementsRequest req,
        @RequestParam(required = false) Map<String, String> params) {

        return cameoViewService.getViews(projectId, refId, req, params);
    }

    @GetMapping("/groups")
    @PreAuthorize("@mss.hasBranchPrivilege(authentication, #projectId, #refId, 'BRANCH_READ', true)")
    public ResponseEntity<?> getGroups(
        @PathVariable String projectId,
        @PathVariable String refId,
        @RequestParam(required = false) Map<String, String> params) {

        return ResponseEntity.ok(null);
    }
}
