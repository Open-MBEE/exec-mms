package org.openmbee.sdvc.crud.controllers.commits;

import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Map;

import org.openmbee.sdvc.core.objects.CommitsRequest;
import org.openmbee.sdvc.core.objects.CommitsResponse;
import org.openmbee.sdvc.crud.controllers.BaseController;
import org.openmbee.sdvc.crud.services.CommitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/projects/{projectId}")
@Tag(name = "Commits")
public class CommitsController extends BaseController {

    private CommitService commitService;

    @Autowired
    public CommitsController(CommitService commitService) {
        this.commitService = commitService;
    }

    @GetMapping(value = "/refs/{refId}/commits")
    @PreAuthorize("@mss.hasProjectPrivilege(authentication, #projectId, 'PROJECT_READ_COMMITS', true)")
    public CommitsResponse getRefCommits(
        @PathVariable String projectId,
        @PathVariable String refId,
        @RequestParam(required = false) String limit,
        @RequestParam(required = false) String maxTimestamp,
        @RequestParam(required = false) Map<String, String> params) {

        return commitService.getRefCommits(projectId, refId, params);
    }

    @GetMapping(value = "/commits/{commitId}")
    @PreAuthorize("@mss.hasProjectPrivilege(authentication, #projectId, 'PROJECT_READ_COMMITS', true)")
    public CommitsResponse getCommit(
        @PathVariable String projectId,
        @PathVariable String commitId) {

        return commitService.getCommit(projectId, commitId);
    }

    @GetMapping(value = "/refs/{refId}/elements/{elementId}/commits")
    @PreAuthorize("@mss.hasProjectPrivilege(authentication, #projectId, 'PROJECT_READ_COMMITS', true)")
    public CommitsResponse getElementCommits(
        @PathVariable String projectId,
        @PathVariable String refId,
        @PathVariable String elementId,
        @RequestParam(required = false) Map<String, String> params) {

        return commitService.getElementCommits(projectId, refId, elementId, params);
    }

    @PutMapping(value = "/commits", consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("@mss.hasProjectPrivilege(authentication, #projectId, 'PROJECT_READ_COMMITS', true)")
    public CommitsResponse getCommits(
        @PathVariable String projectId,
        @RequestBody CommitsRequest req) {

        return commitService.getCommits(projectId, req);
    }
}
