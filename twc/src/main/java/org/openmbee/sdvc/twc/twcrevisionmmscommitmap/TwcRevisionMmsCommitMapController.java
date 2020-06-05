package org.openmbee.sdvc.twc.twcrevisionmmscommitmap;

import org.openmbee.sdvc.core.objects.CommitsResponse;
import org.openmbee.sdvc.crud.controllers.BaseController;
import org.openmbee.sdvc.twc.services.TwcRevisionMmsCommitMapService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/projects/{projectId}/commits/{commitId}")
public class TwcRevisionMmsCommitMapController extends BaseController {

    private TwcRevisionMmsCommitMapService twcRevisionMmsCommitMapService;

    @Autowired
    public void setTwcRevisionMmsCommitMapService(TwcRevisionMmsCommitMapService twcRevisionMmsCommitMapService) {
        this.twcRevisionMmsCommitMapService = twcRevisionMmsCommitMapService;
    }

    @PutMapping(value = "/twc-revision/{revisionId}")
    @PreAuthorize("@mss.hasProjectPrivilege(authentication, #projectId, 'PROJECT_EDIT', true)")
    public CommitsResponse updateTwcRevisionID(
        @PathVariable String projectId,
        @PathVariable String commitId,
        @PathVariable String revisionId) {
        return twcRevisionMmsCommitMapService.updateTwcRevisionID(projectId, commitId, revisionId);
    }
}
