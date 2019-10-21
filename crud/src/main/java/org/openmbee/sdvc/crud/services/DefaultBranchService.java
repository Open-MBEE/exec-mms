package org.openmbee.sdvc.crud.services;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openmbee.sdvc.core.config.Constants;
import org.openmbee.sdvc.core.config.ContextHolder;
import org.openmbee.sdvc.core.exceptions.InternalErrorException;
import org.openmbee.sdvc.core.objects.BranchesResponse;
import org.openmbee.sdvc.core.services.BranchService;
import org.openmbee.sdvc.core.dao.NodeIndexDAO;
import org.openmbee.sdvc.core.exceptions.BadRequestException;
import org.openmbee.sdvc.core.exceptions.DeletedException;
import org.openmbee.sdvc.core.exceptions.NotFoundException;
import org.openmbee.sdvc.data.domains.scoped.Commit;
import org.openmbee.sdvc.data.domains.scoped.Node;
import org.openmbee.sdvc.core.dao.BranchDAO;
import org.openmbee.sdvc.data.domains.scoped.Branch;
import org.openmbee.sdvc.json.RefJson;
import org.openmbee.sdvc.core.dao.CommitDAO;
import org.openmbee.sdvc.core.dao.NodeDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class DefaultBranchService implements BranchService {
    protected final Logger logger = LogManager.getLogger(getClass());

    private BranchDAO branchRepository;

    private CommitDAO commitRepository;

    private CommitService commitService;

    private NodeDAO nodeRepository;

    private NodeIndexDAO nodeIndex;

    @Autowired
    public void setBranchRepository(BranchDAO branchRepository) {
        this.branchRepository = branchRepository;
    }

    @Autowired
    public void setCommitRepository(CommitDAO commitRepository) {
        this.commitRepository = commitRepository;
    }

    @Autowired
    public void setCommitService(CommitService commitService) {
        this.commitService = commitService;
    }

    @Autowired
    public void setNodeRepository(NodeDAO nodeRepository) {
        this.nodeRepository = nodeRepository;
    }

    @Autowired
    public void setNodeIndex(NodeIndexDAO nodeIndex) {
        this.nodeIndex = nodeIndex;
    }

    public BranchesResponse getBranches(String projectId) {
        ContextHolder.setContext(projectId);
        BranchesResponse branchesResponse = new BranchesResponse();
        List<Branch> branches = this.branchRepository.findAll();
        List<RefJson> refs = new ArrayList<>();
        branches.forEach(branch -> {
            refs.add(convertToJson(branch));
        });
        branchesResponse.setBranches(refs);
        return branchesResponse;
    }

    public BranchesResponse getBranch(String projectId, String id) {
        ContextHolder.setContext(projectId);
        BranchesResponse branchesResponse = new BranchesResponse();
        Optional<Branch> branches = this.branchRepository.findByBranchId(id);
        if (!branches.isPresent()) {
            throw new NotFoundException(branchesResponse);
        }
        Branch b = branches.get();
        List<RefJson> refs = new ArrayList<>();
        refs.add(convertToJson(b));
        branchesResponse.setBranches(refs);
        if (b.isDeleted()) {
            throw new DeletedException(branchesResponse);
        }
        return branchesResponse;
    }

    public RefJson createBranch(String projectId, RefJson branch) {
        //TODO sanitize or reject branch id
        Instant now = Instant.now();
        ContextHolder.setContext(projectId);
        Branch b = new Branch();
        b.setBranchId(branch.getId());
        b.setBranchName(branch.getName());
        b.setDescription(branch.getDescription());
        b.setTag(branch.isTag());
        b.setTimestamp(Instant.now());
        logger.info("Saving branch: {}", branch.getId());

        if (branch.getParentRefId() != null) {
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
        }
        if (b.getParentCommit() == null){
            Optional<Branch> ref = branchRepository.findByBranchId(b.getParentRefId());
            if (ref.isPresent()) {
                Optional<Commit> parentCommit = commitRepository.findLatestByRef(ref.get());
                parentCommit.ifPresent(parent -> {
                    b.setParentCommit(parent.getId());
                });
            }
        }
        if (b.getParentCommit() == null) {
            throw new BadRequestException("Parent branch or commit cannot be determined");
            //creating more branches are not allowed until there's at least 1 commit, same as git
        }

        b.setTimestamp(now);
        try {
            branchRepository.save(b);
            Set<String> docIds = new HashSet<>();
            for (Node n: nodeRepository.findAllByDeleted(false)) {
                docIds.add(n.getDocId());
            }
            nodeIndex.addToRef(docIds);
            return convertToJson(b);
        } catch (Exception e) {
            logger.error("Couldn't create branch: {}", branch.getId(), e);
            throw new InternalErrorException(e);
        }
    }

    public BranchesResponse deleteBranch(String projectId, String id) {
        ContextHolder.setContext(projectId);
        BranchesResponse branchesResponse = new BranchesResponse();
        if ("master".equals(id)) {
            throw new BadRequestException(branchesResponse.addMessage("Cannot delete master"));
        }
        Optional<Branch> branch = this.branchRepository.findByBranchId(id);
        if (!branch.isPresent()) {
            throw new NotFoundException(branchesResponse);
        }
        Branch b = branch.get();
        b.setDeleted(true);
        branchRepository.save(b);
        List<RefJson> refs = new ArrayList<>();
        refs.add(convertToJson(b));
        branchesResponse.setBranches(refs);
        return branchesResponse;
    }

    private RefJson convertToJson(Branch branch) {
        RefJson refJson = new RefJson();
        if (branch != null) {
            refJson.setParentRefId(branch.getParentRefId());
            if (branch.getParentCommit() != null) {
                Optional<Commit> c = commitRepository.findById(branch.getParentCommit());
                if (c.isPresent()) {
                    refJson.setParentCommitId(c.get().getDocId());
                }
            }
            refJson.setId(branch.getBranchId());
            refJson.setName(branch.getBranchName());
            refJson.setType(branch.isTag() ? "Tag" : "Branch");
            refJson.setDeleted(branch.isDeleted());
        }
        return refJson;
    }
}
