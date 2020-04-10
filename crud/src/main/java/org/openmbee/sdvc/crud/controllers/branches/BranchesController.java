package org.openmbee.sdvc.crud.controllers.branches;

import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.ArrayList;
import java.util.List;
import javax.transaction.Transactional;
import org.openmbee.sdvc.core.config.Privileges;
import org.openmbee.sdvc.core.exceptions.SdvcException;
import org.openmbee.sdvc.core.objects.RefsRequest;
import org.openmbee.sdvc.core.objects.RefsResponse;
import org.openmbee.sdvc.core.objects.Rejection;
import org.openmbee.sdvc.core.services.BranchService;
import org.openmbee.sdvc.crud.controllers.BaseController;
import org.openmbee.sdvc.core.exceptions.BadRequestException;
import org.openmbee.sdvc.json.RefJson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
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
@Tag(name = "Refs")
public class BranchesController extends BaseController {

    private static final String BRANCH_ID_VALID_PATTERN = "^[\\w-]+$";

    private BranchService branchService;

    @Autowired
    public BranchesController(BranchService branchService) {
        this.branchService = branchService;
    }

    @GetMapping
    public RefsResponse getAllRefs(
        @PathVariable String projectId,
        Authentication auth) {

        RefsResponse res = branchService.getBranches(projectId);
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
    public RefsResponse getBranch(
        @PathVariable String projectId,
        @PathVariable String refId) {

        return branchService.getBranch(projectId, refId);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    @PreAuthorize("@mss.hasProjectPrivilege(authentication, #projectId, 'PROJECT_CREATE_BRANCH', false)")
    public RefsResponse createBranches(
        @PathVariable String projectId,
        @RequestBody RefsRequest projectsPost,
        Authentication auth) {

        if (projectsPost.getRefs().isEmpty()) {
            throw new BadRequestException(new RefsResponse().addMessage("Empty request"));
        }
        RefsResponse response = new RefsResponse();
        for (RefJson branch : projectsPost.getRefs()) {
            try {
                if (branch.getId() == null || branch.getId().isEmpty()) {
                    response.addRejection(new Rejection(branch, 400, "Branch id missing"));
                    continue;
                }
                if(! isBranchIdValid(branch.getId())) {
                    response.addRejection(new Rejection(branch, 400, "Branch id is invalid."));
                    continue;
                }

                RefJson res;

                if(branch.getParentCommitId() == null || branch.getParentCommitId().isEmpty()) {
                    res = branchService.createBranch(projectId, branch);
                } else {
                    //TODO implement branching from historical commit
                    response.addRejection(new Rejection(branch, 400, "Branching from historical commits is not implemented."));
                    continue;
                }

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
    public RefsResponse deleteBranch(
        @PathVariable String projectId,
        @PathVariable String refId) {

        return branchService.deleteBranch(projectId, refId);
    }

    static boolean isBranchIdValid(String branchId) {
        return branchId != null && branchId.matches(BRANCH_ID_VALID_PATTERN);
    }
}
