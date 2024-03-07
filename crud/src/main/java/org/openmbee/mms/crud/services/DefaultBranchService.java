package org.openmbee.mms.crud.services;

import java.util.Collection;

import org.openmbee.mms.core.config.Constants;
import org.openmbee.mms.core.config.ContextHolder;
import org.openmbee.mms.core.config.Formats;
import org.openmbee.mms.core.dao.*;
import org.openmbee.mms.core.exceptions.BadRequestException;
import org.openmbee.mms.core.exceptions.DeletedException;
import org.openmbee.mms.core.exceptions.InternalErrorException;
import org.openmbee.mms.core.exceptions.NotFoundException;
import org.openmbee.mms.core.objects.EventObject;
import org.openmbee.mms.core.objects.RefsResponse;
import org.openmbee.mms.core.services.BranchService;
import org.openmbee.mms.core.services.EventService;
import org.openmbee.mms.core.services.NodeService;
import org.openmbee.mms.data.domains.scoped.Branch;
import org.openmbee.mms.data.domains.scoped.Commit;
import org.openmbee.mms.data.domains.scoped.Node;
import org.openmbee.mms.json.ElementJson;
import org.openmbee.mms.json.RefJson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;

@Service
public class DefaultBranchService implements BranchService {
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private BranchDAO branchRepository;

    private BranchIndexDAO branchIndex;

    private CommitDAO commitRepository;

    private NodeDAO nodeRepository;

    private NodeIndexDAO nodeIndex;

    protected Collection<EventService> eventPublisher;
    protected NodeGetHelper nodeGetHelper;



    @Autowired
    public void setNodeGetHelper(NodeGetHelper nodeGetHelper) {
        this.nodeGetHelper = nodeGetHelper;
    }


    @Autowired
    public void setBranchRepository(BranchDAO branchRepository) {
        this.branchRepository = branchRepository;
    }

    @Autowired
    public void setCommitRepository(CommitDAO commitRepository) {
        this.commitRepository = commitRepository;
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

        Optional<Branch> branchesOption = this.branchRepository.findByBranchId(branch.getId());
        Branch b = branchesOption.orElseGet(Branch::new);
        if (b.isDeleted()) {
            throw new BadRequestException("Bad Request Error: Branch was previously deleted.");
        }

        b.setBranchId(branch.getId());
        b.setBranchName(branch.getName());
        b.setDescription(branch.getDescription());
        b.setTag(branch.isTag());
        b.setTimestamp(now);
        branch.setCreated(Formats.FORMATTER.format(now));
        branch.setDeleted(false);
        branch.setProjectId(projectId);
        boolean fromCommit = branch.getParentCommitId() == null ? false : true;
        branch.setStatus(fromCommit ? "creating" : "created");
        if (branch.getDocId() == null || branch.getDocId().isEmpty()) {
            String docId = branchIndex.createDocId(branch);
            branch.setDocId(docId);
            b.setDocId(docId);
        }
        logger.info("Saving branch: {}", branch.getId());

        if (branch.getParentRefId() != null) {
            b.setParentRefId(branch.getParentRefId());
        } else {
            branch.setParentRefId(Constants.MASTER_BRANCH);
            b.setParentRefId(Constants.MASTER_BRANCH);
        }

        Optional<Branch> refOption = branchRepository.findByBranchId(b.getParentRefId());
        if (refOption.isPresent()) {
            if(branch.getParentCommitId() != null){
                Optional<Commit> commitRequestId = commitRepository.findByCommitId(branch.getParentCommitId());
                commitRequestId.ifPresentOrElse(commit -> {
                    Optional<Commit> parentCommit = commitRepository.findByRefAndTimestamp(refOption.get(), commit.getTimestamp());
                    parentCommit.ifPresent(parent -> {
                        b.setParentCommit(parent.getId());
                        branch.setParentCommitId(parent.getCommitId());
                    });
                },
                    () -> { throw new BadRequestException(new RefsResponse().addMessage("parentCommitId not found " + now.toString()));
                });
            } else {
                Optional<Commit> parentCommit = commitRepository.findLatestByRef(refOption.get());
                parentCommit.ifPresent(parent -> {
                    b.setParentCommit(parent.getId());
                    branch.setParentCommitId(parent.getCommitId()); //commit id is same as its docId
                });
            }
        }

        if (b.getParentCommit() == null) {
            throw new BadRequestException("Parent branch or commit cannot be determined");
            //creating more branches are not allowed until there's at least 1 commit, same as git
        }

        try {
            branchIndex.update(branch);
            branchRepository.save(b);
            if(!fromCommit) {
                Set<String> docIds = new HashSet<>();
                for (Node n: nodeRepository.findAllByDeleted(false)) {
                    docIds.add(n.getDocId());
                }

                try { nodeIndex.addToRef(docIds); } catch(Exception e) {}
            }
            eventPublisher.forEach((pub) -> pub.publish(
                EventObject.create(projectId, branch.getId(), "branch_created", branch)));
            return branch;
        } catch (Exception e) {
            logger.error("Couldn't create branch: {}", branch.getId(), e);
            //TODO should clean up any created tables/rows?
            throw new InternalErrorException(e);
        }
    }
    public RefJson createBranchfromCommit(String projectId, RefJson parentCommitIdRef, NodeService nodeService) {
        Instant now = Instant.now();

        if(parentCommitIdRef.getParentCommitId().isEmpty()){
            throw new BadRequestException(new RefsResponse().addMessage("parentCommitId not provided " + now.toString()));
        }

        ContextHolder.setContext(projectId);
        Optional<Commit> parentCommit = commitRepository.findByCommitId(parentCommitIdRef.getParentCommitId());

        // Get Commit object
        String parentCommitID = parentCommit.map(Commit::getCommitId).orElseThrow(() ->
            new BadRequestException(new RefsResponse().addMessage("parentCommitId not found " + now.toString())));

        // Get Commit parentRef, the branch will be created from this
        String parentRef = parentCommit.map(Commit::getBranchId).orElseThrow(() ->
            new BadRequestException(new RefsResponse().addMessage("Ref from parentCommitId not found"  + now.toString())));

        parentCommitIdRef.setParentRefId(parentRef);
        ContextHolder.setContext(projectId, parentRef);

        RefJson branchFromCommit = this.createBranch(projectId, parentCommitIdRef);
        ContextHolder.setContext(projectId, branchFromCommit.getId());

        // Get current nodes from database
        List<Node> nodes = nodeRepository.findAll();
        // Get elements from index
        Collection<ElementJson> result = nodeGetHelper.processGetJsonFromNodes(nodes, parentCommitID, nodeService)
            .getActiveElementMap().values();

        Map<String, ElementJson> nodeCommitData = new HashMap<>();
        for (ElementJson element : result) {
            nodeCommitData.put(element.getId(), element);
        }

        // Update database table to match index
        Set<String> docIds = new HashSet<>();
        for (Node node : nodes) {
            if(nodeCommitData.containsKey(node.getNodeId())){
                node.setDocId(nodeCommitData.get(node.getNodeId()).getDocId());
                node.setLastCommit(nodeCommitData.get(node.getNodeId()).getCommitId());
                node.setDeleted(false);
                docIds.add(node.getDocId());
            } else {
                node.setDeleted(true);
            }
        }
        nodeRepository.updateAll(nodes);
        branchFromCommit.setStatus("created");
        branchIndex.update(branchFromCommit);
        try { nodeIndex.addToRef(docIds); } catch(Exception e) {}

        return branchFromCommit;
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
