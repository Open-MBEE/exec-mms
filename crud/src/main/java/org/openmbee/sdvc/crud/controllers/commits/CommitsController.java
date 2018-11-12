package org.openmbee.sdvc.crud.controllers.commits;

import org.openmbee.sdvc.crud.controllers.BaseController;
import org.openmbee.sdvc.crud.services.CommitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/projects/{projectId}")
public class CommitsController extends BaseController {

    private CommitService commitService;

    @Autowired
    public CommitsController(CommitService commitService) {
        this.commitService = commitService;
    }

    @GetMapping(value = "commits")
    public ResponseEntity<?> handleGet(
        @PathVariable String projectId,
        @RequestParam Map<String, String> params) {

        return ResponseEntity.ok(null);
    }

    @GetMapping(value = "/commits/{commitId}")
    public ResponseEntity<?> handleCommitGet(
        @PathVariable String projectId,
        @PathVariable String commitId) {

        return ResponseEntity.ok(null);
    }

    @GetMapping(value = "/refs/{refId}/elements/{elementId}/commits")
    public ResponseEntity<?> handleElementCommitsGet(
        @PathVariable String projectId,
        @PathVariable String refId,
        @PathVariable String elementId,
        @RequestParam Map<String, String> params) {

        return ResponseEntity.ok(null);
    }

}
