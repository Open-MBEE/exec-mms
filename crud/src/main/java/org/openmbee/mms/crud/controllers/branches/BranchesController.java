package org.openmbee.mms.crud.controllers.branches;

import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.Optional;
import java.util.UUID;
import org.openmbee.mms.core.config.Privileges;
import org.openmbee.mms.core.exceptions.BadRequestException;
import org.openmbee.mms.core.exceptions.MMSException;
import org.openmbee.mms.core.objects.RefsRequest;
import org.openmbee.mms.core.objects.RefsResponse;
import org.openmbee.mms.core.objects.Rejection;
import org.openmbee.mms.core.services.BranchService;
import org.openmbee.mms.crud.controllers.BaseController;
import org.openmbee.mms.json.RefJson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

import static org.openmbee.mms.core.config.Constants.BRANCH_ID_VALID_PATTERN;

@RestController
@RequestMapping("/projects/{projectId}/refs")
@Tag(name = "Refs")
public class BranchesController extends BaseController {

    private BranchService branchService;

    @Autowired
    public BranchesController(BranchService branchService) {
        this.branchService = branchService;
    }

    @GetMapping
    @PreAuthorize("@mss.hasProjectPrivilege(authentication, #projectId, 'PROJECT_READ', true)")
    public RefsResponse getAllRefs(
        @PathVariable String projectId,
        Authentication auth) {

        getProjectType(projectId);

        RefsResponse res = branchService.getBranches(projectId);
        if (!permissionService.isProjectPublic(projectId)) {
            List<RefJson> filtered = new ArrayList<>();
            for (RefJson ref: res.getRefs()) {
                try {
                    if (mss.hasBranchPrivilege(auth, projectId, ref.getId(),
                        Privileges.BRANCH_READ.name(), false)) {
                        filtered.add(ref);
                    }
                } catch (MMSException e) {
                    logger.warn("error in getting branch permissions: projectId=" +
                        projectId + ", refId=" + ref.getId(), e);
                }
            }
            res.setRefs(filtered);
        }
        return res;
    }

    @GetMapping(value = "/{refId}")
    @PreAuthorize("@mss.hasBranchPrivilege(authentication, #projectId, #refId, 'BRANCH_READ', true)")
    public RefsResponse getRef(
        @PathVariable String projectId,
        @PathVariable String refId) {

        return branchService.getBranch(projectId, refId);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("@mss.hasProjectPrivilege(authentication, #projectId, 'PROJECT_CREATE_BRANCH', false)")
    public RefsResponse createRefs(
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
                    branch.setId(UUID.randomUUID().toString());
                }
                if(!isBranchIdValid(branch.getId())) {
                    response.addRejection(new Rejection(branch, 400, "Branch id is invalid."));
                    continue;
                }

                RefJson res;
                branch.setCreator(auth.getName());
                if (branch.getParentCommitId() == null || branch.getParentCommitId().isEmpty()) {
                    Optional<RefJson> existingOptional = branchPersistence.findById(projectId, branch.getId());
                    if (existingOptional.isPresent()) {
                        //Branch exists, should merge the json
                        branch.merge(existingOptional.get());
                        res = branchService.updateBranch(projectId, branch);
                    } else {
                        res = branchService.createBranch(projectId, branch);
                    }
                } else {
                    //TODO implement branching from historical commit
                    response.addRejection(new Rejection(branch, 400, "Branching from historical commits is not implemented."));
                    continue;
                }

                permissionService.initBranchPerms(projectId, branch.getId(), true, auth.getName());
                response.getRefs().add(res);
            } catch (MMSException e) {
                response.addRejection(new Rejection(branch, e.getCode().value(), e.getMessageObject().toString()));
            }
        }
        if (projectsPost.getRefs().size() == 1) {
            handleSingleResponse(response);
        }
        return response;
    }

    @DeleteMapping("/{refId}")
    @PreAuthorize("@mss.hasBranchPrivilege(authentication, #projectId, #refId, 'BRANCH_DELETE', false)")
    public RefsResponse deleteRef(
        @PathVariable String projectId,
        @PathVariable String refId) {

        return branchService.deleteBranch(projectId, refId);
    }

    static boolean isBranchIdValid(String branchId) {
        return branchId != null && BRANCH_ID_VALID_PATTERN.matcher(branchId).matches();
    }
}
