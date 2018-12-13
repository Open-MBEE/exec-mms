package org.openmbee.sdvc.crud.controllers.commits;

import java.util.Map;
import org.openmbee.sdvc.crud.controllers.BaseController;
import org.openmbee.sdvc.crud.controllers.BaseResponse;
import org.openmbee.sdvc.crud.services.CommitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/projects/{projectId}")
public class CommitsController extends BaseController {

    private CommitService commitService;

    @Autowired
    public CommitsController(CommitService commitService) {
        this.commitService = commitService;
    }

    @GetMapping(value = "/refs/{refId}/commits")
    public ResponseEntity<? extends BaseResponse> handleGet(
        @PathVariable String projectId,
        @PathVariable String refId,
        @RequestParam Map<String, String> params) {

        CommitsResponse res = commitService.getRefCommits(projectId, refId, params);
        return ResponseEntity.ok(res);
    }

    @GetMapping(value = "/commits/{commitId}")
    public ResponseEntity<? extends BaseResponse> handleCommitGet(
        @PathVariable String projectId,
        @PathVariable String commitId) {

        CommitsResponse res = commitService.getCommit(projectId, commitId);
        return ResponseEntity.ok(res);
    }

    @GetMapping(value = "/refs/{refId}/elements/{elementId}/commits")
    public ResponseEntity<? extends BaseResponse> handleElementCommitsGet(
        @PathVariable String projectId,
        @PathVariable String refId,
        @PathVariable String elementId,
        @RequestParam Map<String, Object> params) {

        CommitsResponse res = commitService.getElementCommits(projectId, refId, elementId, params);
        return ResponseEntity.ok(res);
    }

    @PutMapping(value = "/commits")
    public ResponseEntity<? extends BaseResponse> handleBulkGet(
        @PathVariable String projectId,
        @RequestBody CommitsRequest req) {

        CommitsResponse res = commitService.getCommits(projectId, req);
        return ResponseEntity.ok(res);
    }
}
