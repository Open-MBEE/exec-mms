package org.openmbee.mms.twc.twcrevisionmmscommitmap;

import org.openmbee.mms.core.objects.CommitsResponse;
import org.openmbee.mms.crud.controllers.BaseController;
import org.openmbee.mms.json.CommitJson;
import org.openmbee.mms.twc.services.TwcRevisionMmsCommitMapService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/projects/{projectId}")
public class TwcRevisionMmsCommitMapController extends BaseController {

    private TwcRevisionMmsCommitMapService twcRevisionMmsCommitMapService;

    @Autowired
    public void setTwcRevisionMmsCommitMapService(TwcRevisionMmsCommitMapService twcRevisionMmsCommitMapService) {
        this.twcRevisionMmsCommitMapService = twcRevisionMmsCommitMapService;
    }

    @PutMapping(value = "/commits/{commitId}/twc-revision/{revisionId}")
    @PreAuthorize("@mss.hasProjectPrivilege(authentication, #projectId, 'PROJECT_EDIT', true)")
    public CommitsResponse updateTwcRevisionID(
        @PathVariable String projectId,
        @PathVariable String commitId,
        @PathVariable String revisionId) {
        return twcRevisionMmsCommitMapService.updateTwcRevisionID(projectId, commitId, revisionId);
    }

    @GetMapping(value = "/refs/{refId}/twc-revisions")
    @PreAuthorize("@mss.hasProjectPrivilege(authentication, #projectId, 'PROJECT_READ', true)")
    public List<CommitJson> getTwcRevisionList(
        @PathVariable String projectId,
        @PathVariable String refId,
        @RequestParam(value = "reverseOrder", required = false) Boolean reverseOrder,
        @RequestParam(value = "limit", required = false) Integer limit) {
        return twcRevisionMmsCommitMapService.getTwcRevisionList(projectId, refId, reverseOrder, limit);
    }

}
