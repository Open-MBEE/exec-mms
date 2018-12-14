package org.openmbee.sdvc.crud.controllers.branches;

import org.openmbee.sdvc.crud.controllers.BaseController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/projects/{projectId}/refs")
public class BranchesGet extends BaseController {

    @GetMapping(value = {"", "/{refId}"})
    public ResponseEntity<?> handleRequest(
        @PathVariable String projectId,
        @PathVariable(required = false) String refId) {

        if (refId != null) {
            logger.debug("RefId given: ", refId);
            return ResponseEntity.ok(refId);
        } else {
            logger.debug("No RefId given");
            return ResponseEntity.ok("Requesting all projects");
        }
    }
}
