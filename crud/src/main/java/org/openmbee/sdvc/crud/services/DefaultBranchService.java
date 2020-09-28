package org.openmbee.sdvc.crud.services;

import java.time.Instant;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.openmbee.sdvc.core.config.Constants;
import org.openmbee.sdvc.core.config.ContextHolder;
import org.openmbee.sdvc.core.config.Formats;
import org.openmbee.sdvc.core.dao.BranchIndexDAO;
import org.openmbee.sdvc.core.exceptions.InternalErrorException;
import org.openmbee.sdvc.core.objects.RefsResponse;
import org.openmbee.sdvc.core.objects.EventObject;
import org.openmbee.sdvc.core.services.BranchService;
import org.openmbee.sdvc.core.dao.NodeIndexDAO;
import org.openmbee.sdvc.core.exceptions.BadRequestException;
import org.openmbee.sdvc.core.exceptions.DeletedException;
import org.openmbee.sdvc.core.exceptions.NotFoundException;
import org.openmbee.sdvc.core.services.EventService;
import org.openmbee.sdvc.data.domains.scoped.Commit;
import org.openmbee.sdvc.data.domains.scoped.Node;
import org.openmbee.sdvc.core.dao.BranchDAO;
import org.openmbee.sdvc.data.domains.scoped.Branch;
import org.openmbee.sdvc.json.RefJson;
import org.openmbee.sdvc.core.dao.CommitDAO;
import org.openmbee.sdvc.core.dao.NodeDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class DefaultBranchService implements BranchService {
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private BranchDAO branchRepository;

    private BranchIndexDAO branchIndex;

    private CommitDAO commitRepository;

    private CommitService commitService;

    private NodeDAO nodeRepository;

    private NodeIndexDAO nodeIndex;

    protected Collection<EventService> eventPublisher;

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

    @Autowired
    public void setBranchIndex(BranchIndexDAO branchIndex) {
        this.branchIndex = branchIndex;
    }

    @Autowired
    public void setEventPublisher(Collection<EventService> eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    public RefsResponse getBranches(String projectId) {
        ContextHolder.setContext(projectId);
        RefsResponse branchesResponse = new RefsResponse();
        List<Branch> branches = this.branchRepository.findAll();
        Set<String> docIds = new HashSet<>();
        branches.forEach(branch -> {
            docIds.add(branch.getDocId());
        });
        branchesResponse.setRefs(branchIndex.findAllById(docIds));
        return branchesResponse;
    }

    public RefsResponse getBranch(String projectId, String id) {
        ContextHolder.setContext(projectId);
        RefsResponse branchesResponse = new RefsResponse();
        Optional<Branch> branchesOption = this.branchRepository.findByBranchId(id);
        if (!branchesOption.isPresent()) {
            throw new NotFoundException(branchesResponse);
        }
        Branch b = branchesOption.get();
        List<RefJson> refs = new ArrayList<>();
        Optional<RefJson> refOption = branchIndex.findById(b.getDocId());
        if (!refOption.isPresent()) {
            logger.error("DefaultBranchService: JSON Not found for {} with docId: {}",
                b.getBranchId(), b.getDocId());
            throw new NotFoundException(branchesResponse);
        }
        refs.add(refOption.get());
        branchesResponse.setRefs(refs);
        if (b.isDeleted()) {
            throw new DeletedException(branchesResponse);
        }
        return branchesResponse;
    }

    public RefJson createBranch(String projectId, RefJson branch) {
        Instant now = Instant.now();
        ContextHolder.setContext(projectId);
        Branch b = new Branch();

        b.setBranchId(branch.getId());
        b.setBranchName(branch.getName());
        b.setDescription(branch.getDescription());
        b.setTag(branch.isTag());
        b.setTimestamp(now);
        branch.setCreated(Formats.FORMATTER.format(now));
        branch.setDeleted(false);
        branch.setProjectId(projectId);

        if (branch.getDocId() == null || branch.getDocId().isEmpty()) {
            String uuid = UUID.randomUUID().toString();
            branch.setDocId(uuid);
            b.setDocId(uuid);
        }
        logger.info("Saving branch: {}", branch.getId());

        if (branch.getParentRefId() != null) {
            b.setParentRefId(branch.getParentRefId());
        } else {
            branch.setParentRefId(Constants.MASTER_BRANCH);
            b.setParentRefId(Constants.MASTER_BRANCH);
        }

        //This service cannot create branches from historic versions
        if (branch.getParentCommitId() != null) {
            throw new BadRequestException("Internal Error: Invalid branch creation logic.");
        }

        Optional<Branch> refOption = branchRepository.findByBranchId(b.getParentRefId());
        if (refOption.isPresent()) {
            Optional<Commit> parentCommit = commitRepository.findLatestByRef(refOption.get());
            parentCommit.ifPresent(parent -> {
                b.setParentCommit(parent.getId());
                branch.setParentCommitId(parent.getDocId()); //commit id is same as its docId
            });
        }

        if (b.getParentCommit() == null) {
            throw new BadRequestException("Parent branch or commit cannot be determined");
            //creating more branches are not allowed until there's at least 1 commit, same as git
        }

        try {
            branchIndex.update(branch);
            branchRepository.save(b);
            Set<String> docIds = new HashSet<>();
            for (Node n: nodeRepository.findAllByDeleted(false)) {
                docIds.add(n.getDocId());
            }
            nodeIndex.addToRef(docIds);
            eventPublisher.forEach((pub) -> pub.publish(
                EventObject.create(projectId, branch.getId(), "branch_created", branch)));
            return branch;
        } catch (Exception e) {
            logger.error("Couldn't create branch: {}", branch.getId(), e);
            throw new InternalErrorException(e);
        }
    }

    public RefsResponse deleteBranch(String projectId, String id) {
        ContextHolder.setContext(projectId);
        RefsResponse branchesResponse = new RefsResponse();
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
        RefJson refJson = new RefJson().setDocId(b.getDocId()).setDeleted(true)
            .setProjectId(projectId).setId(id);
        refs.add(branchIndex.update(refJson));
        branchesResponse.setRefs(refs);
        return branchesResponse;
    }
}
