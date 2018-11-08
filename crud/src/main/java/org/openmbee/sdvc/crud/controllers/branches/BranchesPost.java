package org.openmbee.sdvc.crud.controllers.branches;

import java.time.Instant;
import org.openmbee.sdvc.crud.config.DbContextHolder;
import org.openmbee.sdvc.crud.controllers.BaseController;
import org.openmbee.sdvc.crud.controllers.BaseResponse;
import org.openmbee.sdvc.crud.controllers.ErrorResponse;
import org.openmbee.sdvc.crud.domains.Branch;
import org.openmbee.sdvc.crud.domains.Commit;
import org.openmbee.sdvc.crud.repositories.branch.BranchDAOImpl;
import org.openmbee.sdvc.crud.services.DatabaseDefinitionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/projects/{projectId}/refs")
public class BranchesPost extends BaseController {

    private BranchDAOImpl branchRepository;

    private DatabaseDefinitionService branchesOperations;

    @Autowired
    public BranchesPost(BranchDAOImpl branchRepository, DatabaseDefinitionService branchesOperations) {
        this.branchRepository = branchRepository;
        this.branchesOperations = branchesOperations;
    }

    @PostMapping
    public ResponseEntity<? extends BaseResponse> handleRequest(
        @PathVariable String projectId,
        @RequestBody BranchesRequest projectsPost) {
        if (!projectsPost.getBranches().isEmpty()) {
            logger.info("JSON parsed properly");
            BranchesResponse response = new BranchesResponse();
            Instant now = Instant.now();

            for (Branch branch : projectsPost.getBranches()) {
                DbContextHolder.setContext(projectId);

                logger.info("Saving branch: {}", branch.getBranchId());

                if (branch.getParentRefId() != null) {
                    Branch parentRef = branchRepository.findByBranchId(branch.getParentRefId());
                    branch.setParentRef(parentRef);
                } else {
                    branch.setParentRef(branchRepository.findByBranchId("master"));
                }

                if (branch.getParentCommitId() != null) {
                    Commit parentCommit = commitRepository.findByCommitId(branch.getParentCommitId());
                    branch.setParentCommit(parentCommit);
                } else {
                    branch.setParentCommit(commitRepository.findLatest());
                }

                branch.setTimestamp(now);

                Branch saved = branchRepository.save(branch);
                try {
                    DbContextHolder.setContext(projectId, saved.getBranchId());
                    if (branchesOperations.createBranch()) {
                        branchesOperations.copyTablesFromParent(branch.getBranchId(),
                            branch.getParentRef().getBranchId(), branch.getParentCommitId());
                    }
                    response.getBranches().add(saved);
                } catch (Exception e) {
                    branchRepository.delete(saved);
                    logger.error("Couldn't create branch: {}", branch.getBranchId());
                    logger.error(e);
                }

            }

            return ResponseEntity.ok(response);
        }
        logger.debug("Bad Request");
        ErrorResponse err = new ErrorResponse();
        err.setCode(400);
        err.setError("Bad Request");
        return ResponseEntity.badRequest().body(err);
    }
}
