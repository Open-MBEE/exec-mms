package org.openmbee.sdvc.crud.controllers.elements;

import java.time.Instant;
import org.openmbee.sdvc.crud.controllers.BaseController;
import org.openmbee.sdvc.crud.controllers.BaseResponse;
import org.openmbee.sdvc.crud.controllers.ErrorResponse;
import org.openmbee.sdvc.crud.config.DbContextHolder;
import org.openmbee.sdvc.crud.domains.Commit;
import org.openmbee.sdvc.crud.domains.CommitType;
import org.openmbee.sdvc.crud.domains.Node;
import org.openmbee.sdvc.crud.repositories.node.NodeDAOImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/projects/{projectId}/refs/{refId}/elements")
public class ElementsPost extends BaseController {

    private NodeDAOImpl nodeRepository;

    @Autowired
    public ElementsPost(NodeDAOImpl nodeRepository) {
        this.nodeRepository = nodeRepository;
    }

    @PostMapping
    public ResponseEntity<? extends BaseResponse> handleRequest(
        @PathVariable
            String projectId,
        @PathVariable
            String refId,
        @RequestBody ElementsPostRequest elementsPost) {
        if (!elementsPost.getElements().isEmpty()) {
            logger.info("JSON parsed properly");
            DbContextHolder.setContext(projectId, refId);
            ElementsResponse response = new ElementsResponse();

            for (Element element : elementsPost.getElements()) {
                logger.info("Saving element with id: {}", element.getId());
                Node node = element.toNode();
                this.nodeRepository.save(node);
                response.getElements().add(node);
            }

            Commit commit = new Commit();
            commit.setBranchId(DbContextHolder.getContext().getBranchId());
            commit.setCommitType(CommitType.COMMIT);
            commit.setCreator("admin");
            commit.setElasticId("test");
            commit.setTimestamp(Instant.now());

            this.commitRepository.save(commit);

            return ResponseEntity.ok(response);
        }
        logger.debug("Bad Request");
        ErrorResponse err = new ErrorResponse();
        err.setCode(400);
        err.setError("Bad Request");
        return ResponseEntity.badRequest().body(err);
    }
}
