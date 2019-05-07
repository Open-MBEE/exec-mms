package org.openmbee.sdvc.crud.controllers.branches;

import org.openmbee.sdvc.crud.controllers.BaseController;
import org.openmbee.sdvc.crud.services.BranchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/projects/{projectId}/refs")
public class BranchesGet extends BaseController {

    private BranchService branchService;

    @Autowired
    public BranchesGet(BranchService branchService) {
        this.branchService = branchService;
    }

    @GetMapping(value = {"", "/{refId}"})
    public ResponseEntity<?> handleRequest(@PathVariable String projectId, @PathVariable(required = false) String refId) {
        if (refId != null) {
            BranchesResponse res = branchService.getBranch(projectId, refId);
            if (res.getBranches().isEmpty()) {
                ResponseEntity.notFound();
            }
            return ResponseEntity.ok(res);
        } else {
            BranchesResponse res = branchService.getBranches(projectId);
            if (res.getBranches().isEmpty()) {
                ResponseEntity.notFound();
            }
            return ResponseEntity.ok(res);
        }
    }
}
