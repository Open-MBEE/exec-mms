package org.openmbee.sdvc.crud.services;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openmbee.sdvc.core.config.Constants;
import org.openmbee.sdvc.core.config.ContextHolder;
import org.openmbee.sdvc.core.dao.BranchIndexDAO;
import org.openmbee.sdvc.core.exceptions.InternalErrorException;
import org.openmbee.sdvc.core.objects.BranchesResponse;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class DefaultBranchService implements BranchService {
    protected final Logger logger = LogManager.getLogger(getClass());

    private BranchDAO branchRepository;

    private BranchIndexDAO branchIndex;

    private CommitDAO commitRepository;

    private CommitService commitService;

    private NodeDAO nodeRepository;

    private NodeIndexDAO nodeIndex;

    protected Optional<EventService> eventPublisher;

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
    public void setEventPublisher(Optional<EventService> eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    public BranchesResponse getBranches(String projectId) {
        ContextHolder.setContext(projectId);
        BranchesResponse branchesResponse = new BranchesResponse();
        List<Branch> branches = this.branchRepository.findAll();
        Set<String> docIds = new HashSet<>();
        branches.forEach(branch -> {
            docIds.add(branch.getDocId());
        });
        //TODO: Paginate?
        branchesResponse.setRefs(branchIndex.findAllById(docIds));
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
        Optional<RefJson> branchJson = branchIndex.findById(b.getDocId());
        if (!branchJson.isPresent()) {
            logger.error("DefaultBranchService: JSON Not found for {} with docId: {}",
                b.getBranchId(), b.getDocId());
            throw new NotFoundException(branchesResponse);
        }
        refs.add(branchJson.get());
        branchesResponse.setRefs(refs);
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

        branchIndex.update(branch);

        Optional<Branch> ref = branchRepository.findByBranchId(b.getParentRefId());
        if (ref.isPresent()) {
            Optional<Commit> parentCommit = commitRepository.findLatestByRef(ref.get());
            parentCommit.ifPresent(parent -> {
                b.setParentCommit(parent.getId());
            });
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
            RefJson res = convertToJson(b);
            res.setProjectId(projectId);
            eventPublisher.ifPresent((pub) -> pub.publish(
                EventObject.create(projectId, res.getId(), "branch_created", res)));
            return res;
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
        Optional<RefJson> refOptional = branchIndex.findById(b.getDocId());
        if(refOptional.isPresent()) {
            RefJson refJson = refOptional.get();
            refJson.setDeleted(true);
            branchIndex.update(refJson);
            refs.add(refJson);
            branchesResponse.setRefs(refs);
        }
        return branchesResponse;
    }

    private RefJson convertToJson(Branch branch) {
        RefJson refJson = new RefJson();
        if (branch != null) {
            refJson.setParentRefId(branch.getParentRefId());
            if (branch.getParentCommit() != null) {
                Optional<Commit> c = commitRepository.findById(branch.getParentCommit());
                c.ifPresent(parent -> {
                    refJson.setParentCommitId(parent.getDocId());
                });
            }
            refJson.setId(branch.getBranchId());
            refJson.setName(branch.getBranchName());
            refJson.setType(branch.isTag() ? "Tag" : "Branch");
            refJson.setDeleted(branch.isDeleted());
        }
        return refJson;
    }
}
