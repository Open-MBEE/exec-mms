package org.openmbee.sdvc.crud.controllers.branches;

import java.util.ArrayList;
import java.util.List;
import javax.transaction.Transactional;
import org.openmbee.sdvc.core.config.Privileges;
import org.openmbee.sdvc.core.exceptions.SdvcException;
import org.openmbee.sdvc.core.objects.BranchesRequest;
import org.openmbee.sdvc.core.objects.BranchesResponse;
import org.openmbee.sdvc.core.objects.Rejection;
import org.openmbee.sdvc.core.services.BranchService;
import org.openmbee.sdvc.crud.controllers.BaseController;
import org.openmbee.sdvc.core.exceptions.BadRequestException;
import org.openmbee.sdvc.json.RefJson;
import org.springframework.beans.factory.annotation.Autowired;
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

    @GetMapping
    public BranchesResponse getAllBranches(
        @PathVariable String projectId,
        Authentication auth) {

        BranchesResponse res = branchService.getBranches(projectId);
        if (!permissionService.isProjectPublic(projectId)) {
            List<RefJson> filtered = new ArrayList<>();
            for (RefJson ref: res.getRefs()) {
                if (mss.hasBranchPrivilege(auth, projectId, ref.getId(), Privileges.BRANCH_READ.name(), false)) {
                    filtered.add(ref);
                }
            }
            res.setRefs(filtered);
        }
        return res;
    }

    @GetMapping(value = "/{refId}")
    @PreAuthorize("@mss.hasBranchPrivilege(authentication, #projectId, #refId, 'BRANCH_READ', true)")
    public BranchesResponse getBranch(
        @PathVariable String projectId,
        @PathVariable String refId,
        Authentication auth) {

        return branchService.getBranch(projectId, refId);
    }

    @PostMapping
    @Transactional
    @PreAuthorize("@mss.hasProjectPrivilege(authentication, #projectId, 'PROJECT_CREATE_BRANCH', false)")
    public BranchesResponse createBranches(
        @PathVariable String projectId,
        @RequestBody BranchesRequest projectsPost,
        Authentication auth) {

        if (projectsPost.getRefs().isEmpty()) {
            throw new BadRequestException(new BranchesResponse().addMessage("Empty request"));
        }
        BranchesResponse response = new BranchesResponse();
        for (RefJson branch : projectsPost.getRefs()) {
            try {
                RefJson res = branchService.createBranch(projectId, branch);
                permissionService.initBranchPerms(projectId, branch.getId(), true, auth.getName());
                response.getRefs().add(res);
            } catch (SdvcException e) {
                response.addRejection(new Rejection(branch, e.getCode().value(), e.getMessageObject().toString()));
            }
        }
        if (projectsPost.getRefs().size() == 1) {
            handleSingleResponse(response);
        }
        return response;
    }

    @DeleteMapping("/{refId}")
    @Transactional
    @PreAuthorize("@mss.hasBranchPrivilege(authentication, #projectId, #refId, 'BRANCH_DELETE', false)")
    public BranchesResponse deleteBranch(
        @PathVariable String projectId,
        @PathVariable String refId) {

        return branchService.deleteBranch(projectId, refId);
    }
}
