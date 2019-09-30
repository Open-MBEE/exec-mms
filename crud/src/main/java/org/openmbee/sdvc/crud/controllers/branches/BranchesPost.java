package org.openmbee.sdvc.crud.controllers.branches;

import java.time.Instant;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import javax.transaction.Transactional;

import org.openmbee.sdvc.core.config.Privileges;
import org.openmbee.sdvc.core.objects.BranchesRequest;
import org.openmbee.sdvc.core.objects.BranchesResponse;
import org.openmbee.sdvc.core.config.ContextHolder;
import org.openmbee.sdvc.core.services.NodeIndexDAO;
import org.openmbee.sdvc.crud.controllers.BaseController;
import org.openmbee.sdvc.core.objects.BaseResponse;
import org.openmbee.sdvc.core.config.Constants;
import org.openmbee.sdvc.crud.exceptions.ForbiddenException;
import org.openmbee.sdvc.crud.services.CommitService;
import org.openmbee.sdvc.data.domains.scoped.Branch;
import org.openmbee.sdvc.data.domains.scoped.Commit;
import org.openmbee.sdvc.crud.exceptions.BadRequestException;
import org.openmbee.sdvc.data.domains.scoped.Node;
import org.openmbee.sdvc.rdb.repositories.branch.BranchDAO;
import org.openmbee.sdvc.rdb.repositories.commit.CommitDAO;
import org.openmbee.sdvc.rdb.config.DatabaseDefinitionService;
import org.openmbee.sdvc.json.RefJson;
import org.openmbee.sdvc.rdb.repositories.node.NodeDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/projects/{projectId}/refs")
public class BranchesPost extends BaseController {

    private BranchDAO branchRepository;

    private DatabaseDefinitionService branchesOperations;

    private CommitDAO commitRepository;

    private CommitService commitService;

    private NodeDAO nodeRepository;

    private NodeIndexDAO nodeIndex;

    @Autowired
    public BranchesPost(BranchDAO branchRepository, DatabaseDefinitionService branchesOperations,
        CommitDAO commitRepository, CommitService commitService, NodeDAO nodeRepository, NodeIndexDAO nodeIndex) {
        this.branchRepository = branchRepository;
        this.branchesOperations = branchesOperations;
        this.commitRepository = commitRepository;
        this.commitService = commitService;
        this.nodeRepository = nodeRepository;
        this.nodeIndex = nodeIndex;
    }

    @PostMapping
    @Transactional
    @PreAuthorize("@mss.hasProjectPrivilege(authentication, #projectId, 'PROJECT_CREATE_BRANCH', false)")
    public ResponseEntity<? extends BaseResponse> handleRequest(
            @PathVariable String projectId,
            @RequestBody BranchesRequest projectsPost,
            Authentication auth) {

        if (projectsPost.getRefs().isEmpty()) {
            throw new BadRequestException(new BranchesResponse().addMessage("Empty request"));
        }

        BranchesResponse response = new BranchesResponse();
        Instant now = Instant.now();

        for (RefJson branch : projectsPost.getRefs()) {
            ContextHolder.setContext(projectId);
            Branch b = new Branch();
            b.setBranchId(branch.getId());
            b.setBranchName(branch.getName());
            b.setDescription(branch.getDescription());
            b.setTag(branch.isTag());
            b.setTimestamp(Instant.now());
            logger.info("Saving branch: {}", branch.getId());

            if (branch.getParentRefId() != null) {
                //Branch parentRef = branchRepository.findByBranchId(branch.getParentRefId());
                b.setParentRefId(branch.getParentRefId());
            } else {
                b.setParentRefId(Constants.MASTER_BRANCH);
            }

            if (branch.getParentCommitId() != null) {
                Optional<Commit> parentCommit = commitRepository
                    .findByCommitId(branch.getParentCommitId());
                if (parentCommit.isPresent()) {
                    b.setParentCommit(parentCommit.get().getId());
                }
            } else {
                Optional<Branch> ref = branchRepository.findByBranchId(b.getParentRefId());
                if (ref.isPresent()) {
                    Optional<Commit> parentCommit = commitRepository.findLatestByRef(ref.get());
                    parentCommit.ifPresent(parent -> {
                        b.setParentCommit(parent.getId());
                    });
                }
            }

            b.setTimestamp(now);

            Branch saved = branchRepository.save(b);
            try {
                ContextHolder.setContext(projectId, saved.getBranchId());
                if (branchesOperations.createBranch()) {
                    branchesOperations.copyTablesFromParent(saved.getBranchId(),
                        b.getParentRefId(), branch.getParentCommitId());
                }
                response.getBranches().add(branch);
                permissionService.initBranchPerms(projectId, branch.getId(), true, auth.getName());
                ContextHolder.setContext(projectId, saved.getBranchId());
                Set<String> docIds = new HashSet<>();
                for (Node n: nodeRepository.findAllByDeleted(false)) {
                    docIds.add(n.getDocId());
                }
                nodeIndex.addToRef(docIds);
            } catch (Exception e) {
                branchRepository.delete(saved);
                logger.error("Couldn't create branch: {}", branch.getId());
                logger.error(e);
            }
        }
        return ResponseEntity.ok(response);
    }
}
