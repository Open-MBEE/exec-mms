package org.openmbee.sdvc.crud.controllers.elements;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import org.openmbee.sdvc.crud.controllers.BaseController;
import org.openmbee.sdvc.crud.controllers.BaseResponse;
import org.openmbee.sdvc.crud.controllers.Constants;
import org.openmbee.sdvc.crud.controllers.ErrorResponse;
import org.openmbee.sdvc.crud.config.DbContextHolder;
import org.openmbee.sdvc.crud.controllers.projects.ProjectJson;
import org.openmbee.sdvc.crud.domains.Commit;
import org.openmbee.sdvc.crud.domains.CommitType;
import org.openmbee.sdvc.crud.domains.Node;
import org.openmbee.sdvc.crud.repositories.node.NodeDAOImpl;
import org.openmbee.sdvc.crud.services.NodeService;
import org.openmbee.sdvc.crud.services.NodeServiceFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/projects/{projectId}/refs/{refId}/elements")
public class ElementsPost extends BaseController {

    private NodeServiceFactory nodeServiceFactory;

    @Autowired
    public ElementsPost(NodeServiceFactory nodeServiceFactory) {
        this.nodeServiceFactory = nodeServiceFactory;
    }

    @PostMapping
    public ResponseEntity<? extends BaseResponse> handleRequest(
        @PathVariable
            String projectId,
        @PathVariable
            String refId,
        @RequestBody ElementsPostRequest elementsPost,
        @RequestParam Map<String, String> params) {
        if (!elementsPost.getElements().isEmpty()) {
            String type = "sysml";
            NodeService nodeService = nodeServiceFactory.getNodeService(type);
            logger.info("JSON parsed properly");
            ElementsResponse response = nodeService.post(projectId, refId, elementsPost, params);
            return ResponseEntity.ok(response);
        }
        logger.debug("Bad Request");
        ErrorResponse err = new ErrorResponse();
        err.setCode(400);
        err.setError("Bad Request");
        return ResponseEntity.badRequest().body(err);
    }
}
