package org.openmbee.sdvc.cameo.controllers;

import java.util.ArrayList;
import java.util.Map;
import org.openmbee.sdvc.cameo.services.CameoNodeService;
import org.openmbee.sdvc.core.objects.ProjectsResponse;
import org.openmbee.sdvc.crud.controllers.BaseController;
import org.openmbee.sdvc.json.MountJson;
import org.openmbee.sdvc.json.ProjectJson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/projects/{projectId}/refs/{refId}")
public class VeController extends BaseController {

    private CameoNodeService nodeService;

    @Autowired
    public VeController(CameoNodeService nodeService) {
        this.nodeService = nodeService;
    }

    @GetMapping("/mounts")
    @PreAuthorize("@mss.hasBranchPrivilege(authentication, #projectId, #refId, 'BRANCH_READ', true)")
    public MountsResponse getMounts(
        @PathVariable String projectId,
        @PathVariable String refId,
        @RequestParam Map<String, String> params) {

        MountJson json = nodeService.getProjectUsages(projectId, refId, params.get("commitId"), new ArrayList<>());
        MountsResponse res = new MountsResponse();
        res.getProjects().add(json);
        return res;
    }

    @GetMapping("/documents")
    @PreAuthorize("@mss.hasBranchPrivilege(authentication, #projectId, #refId, 'BRANCH_READ', true)")
    public ResponseEntity<?> getDocuments(
        @PathVariable String projectId,
        @PathVariable String refId,
        @RequestParam Map<String, String> params) {

        return ResponseEntity.ok(null);
    }


    @GetMapping("/groups")
    @PreAuthorize("@mss.hasBranchPrivilege(authentication, #projectId, #refId, 'BRANCH_READ', true)")
    public ResponseEntity<?> getGroups(
        @PathVariable String projectId,
        @PathVariable String refId,
        @RequestParam Map<String, String> params) {

        return ResponseEntity.ok(null);
    }
}
