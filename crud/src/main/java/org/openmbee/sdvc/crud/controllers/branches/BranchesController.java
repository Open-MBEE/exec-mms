package org.openmbee.sdvc.crud.controllers.branches;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.transaction.Transactional;
import org.openmbee.sdvc.core.config.Privileges;
import org.openmbee.sdvc.core.exceptions.SdvcException;
import org.openmbee.sdvc.core.objects.BaseResponse;
import org.openmbee.sdvc.core.objects.BranchesRequest;
import org.openmbee.sdvc.core.objects.BranchesResponse;
import org.openmbee.sdvc.core.services.BranchService;
import org.openmbee.sdvc.crud.controllers.BaseController;
import org.openmbee.sdvc.core.exceptions.BadRequestException;
import org.openmbee.sdvc.json.RefJson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/projects/{projectId}/refs")
public class BranchesController extends BaseController {

    private BranchService branchService;

    @Autowired
    public BranchesController(BranchService branchService) {
        this.branchService = branchService;
    }

    @GetMapping(value = {"", "/{refId}"})
    @PreAuthorize("#refId == null || @mss.hasBranchPrivilege(authentication, #projectId, #refId, 'BRANCH_READ', true)")
    public ResponseEntity<?> handleRequest(
        @PathVariable String projectId,
        @PathVariable(required = false) String refId,
        Authentication auth) {

        if (refId != null) {
            return ResponseEntity.ok(branchService.getBranch(projectId, refId));
        } else {
            BranchesResponse res = branchService.getBranches(projectId);
            if (!permissionService.isProjectPublic(projectId)) {
                List<RefJson> filtered = new ArrayList<>();
                for (RefJson ref: res.getBranches()) {
                    if (mss.hasBranchPrivilege(auth, projectId, ref.getId(), Privileges.BRANCH_READ.name(), false)) {
                        filtered.add(ref);
                    }
                }
                res.setBranches(filtered);
            }
            return ResponseEntity.ok(res);
        }
    }

    @PostMapping
    @Transactional
    @PreAuthorize("@mss.hasProjectPrivilege(authentication, #projectId, 'PROJECT_CREATE_BRANCH', false)")
    public ResponseEntity<? extends BaseResponse> handleRequest(
        @PathVariable String projectId,
        @RequestBody BranchesRequest projectsPost,
        Authentication auth) {

        if (projectsPost.getRefs().isEmpty()) {
            throw new BadRequestException(new BranchesResponse().addMessage("Empty request"));
        }
        BranchesResponse response = new BranchesResponse();
        List<Map> rejected = new ArrayList<>();
        response.setRejected(rejected);
        for (RefJson branch : projectsPost.getRefs()) {
            try {
                RefJson res = branchService.createBranch(projectId, branch);
                permissionService.initBranchPerms(projectId, branch.getId(), true, auth.getName());
                response.getBranches().add(res);
            } catch (SdvcException e) {
                Map<String, Object> rejection = new HashMap<>();
                rejection.put("message", e.getMessageObject());
                rejection.put("code", e.getCode().value());
                rejection.put("ref", branch);
                rejected.add(rejection);
            }
        }
        if (projectsPost.getRefs().size() == 1) {
            handleSingleResponse(response);
        }
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{refId}")
    @Transactional
    @PreAuthorize("@mss.hasBranchPrivilege(authentication, #projectId, #refId, 'BRANCH_DELETE', false)")
    public ResponseEntity<? extends BaseResponse> deleteBranch(
        @PathVariable String projectId,
        @PathVariable String refId) {

        return ResponseEntity.ok(branchService.deleteBranch(projectId, refId));
    }
}
