package org.openmbee.mms.cameo.controllers;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.ArrayList;
import java.util.Map;
import org.openmbee.mms.cameo.services.CameoViewService;
import org.openmbee.mms.core.objects.ElementsRequest;
import org.openmbee.mms.core.objects.ElementsResponse;
import org.openmbee.mms.crud.controllers.BaseController;
import org.openmbee.mms.json.MountJson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/projects/{projectId}/refs/{refId}")
@Tag(name = "Views")
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
        @RequestParam(required = false) String commitId,
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
        @RequestParam(required = false) String commitId,
        @RequestParam(required = false) Map<String, String> params) {

        ElementsResponse docs = cameoViewService.getDocuments(projectId, refId, params);
        return (new DocumentsResponse()).setDocuments(docs.getElements());
    }

    @GetMapping("/views/{viewId}")
    @PreAuthorize("@mss.hasBranchPrivilege(authentication, #projectId, #refId, 'BRANCH_READ', true)")
    public ElementsResponse getView(
        @PathVariable String projectId,
        @PathVariable String refId,
        @PathVariable String viewId,
        @RequestParam(required = false) String commitId,
        @RequestParam(required = false) Map<String, String> params) {

        ElementsResponse res = cameoViewService.getView(projectId, refId, viewId, params);
        handleSingleResponse(res);
        return res;
    }

    @PutMapping("/views")
    @PreAuthorize("@mss.hasBranchPrivilege(authentication, #projectId, #refId, 'BRANCH_READ', true)")
    public ElementsResponse getViews(
        @PathVariable String projectId,
        @PathVariable String refId,
        @RequestBody ElementsRequest req,
        @RequestParam(required = false) String commitId,
        @RequestParam(required = false) Map<String, String> params) {

        return cameoViewService.getViews(projectId, refId, req, params);
    }

    @PostMapping("/views")
    @PreAuthorize("@mss.hasBranchPrivilege(authentication, #projectId, #refId, 'BRANCH_EDIT_CONTENT', false)")
    public ElementsResponse createOrUpdateViews(
        @PathVariable String projectId,
        @PathVariable String refId,
        @RequestBody ElementsRequest req,
        @RequestParam(required = false) String overwrite,
        @RequestParam(required = false) Map<String, String> params,
        @Parameter(hidden = true) Authentication auth) {

        ElementsResponse res = cameoViewService.createOrUpdate(projectId, refId, req, params, auth.getName());
        cameoViewService.addChildViews(res, params);
        return res;
    }

    @GetMapping("/groups")
    @PreAuthorize("@mss.hasBranchPrivilege(authentication, #projectId, #refId, 'BRANCH_READ', true)")
    public GroupsResponse getGroups(
        @PathVariable String projectId,
        @PathVariable String refId,
        @RequestParam(required = false) Map<String, String> params) {

        ElementsResponse groups = cameoViewService.getGroups(projectId, refId, params);
        return (new GroupsResponse()).setGroups(groups.getElements());
    }
}
