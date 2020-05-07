package org.openmbee.sdvc.artifacts.controllers;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.openmbee.sdvc.core.exceptions.BadRequestException;
import org.openmbee.sdvc.core.objects.ElementsResponse;
import org.openmbee.sdvc.core.security.MethodSecurityService;
import org.openmbee.sdvc.core.services.ArtifactService;
import org.openmbee.sdvc.core.services.NodeService;
import org.openmbee.sdvc.crud.controllers.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/projects/{projectId}/refs/{refId}")
@Tag(name = "Artifacts")
public class ArtifactController extends BaseController {

    private ArtifactService artifactService;

    @Autowired
    public void setArtifactService(ArtifactService artifactService) {
        this.artifactService = artifactService;
    }

    @PostMapping(value = "elements/{elementId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("@mss.hasBranchPrivilege(authentication, #projectId, #refId, 'BRANCH_EDIT_CONTENT', false)")
    public ElementsResponse createOrUpdateArtifacts(
        @PathVariable String projectId,
        @PathVariable String refId,
        @PathVariable String elementId,
        @RequestParam("file") MultipartFile file,
        @RequestParam(required = false) Map<String, String> params,
        Authentication auth) {

        ElementsResponse response = new ElementsResponse();
        if (! file.isEmpty()) {
            return artifactService.createOrUpdate(projectId, refId, elementId, file, auth.getName(), params);
        }
        throw new BadRequestException(response.addMessage("Empty"));
    }
}
