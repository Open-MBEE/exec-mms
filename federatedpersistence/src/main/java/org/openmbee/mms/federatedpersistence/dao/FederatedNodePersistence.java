package org.openmbee.mms.federatedpersistence.dao;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.openmbee.mms.core.config.ContextHolder;
import org.openmbee.mms.core.dao.*;
import org.openmbee.mms.core.exceptions.BadRequestException;
import org.openmbee.mms.core.exceptions.InternalErrorException;
import org.openmbee.mms.core.services.NodeChangeInfo;
import org.openmbee.mms.core.services.NodeGetInfo;
import org.openmbee.mms.data.dao.*;
import org.openmbee.mms.federatedpersistence.domain.*;
import org.openmbee.mms.data.domains.scoped.Branch;
import org.openmbee.mms.data.domains.scoped.Commit;
import org.openmbee.mms.data.domains.scoped.Node;
import org.openmbee.mms.json.CommitJson;
import org.openmbee.mms.json.ElementJson;
import org.openmbee.mms.json.RefJson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Component
public class FederatedNodePersistence implements NodePersistence {
    protected static final Logger logger = LoggerFactory.getLogger(FederatedNodePersistence.class);

    protected CommitPersistence commitPersistence;

    @Autowired
    public void setCommitPersistence(CommitPersistence commitPersistence) {
    }

    @Value("${mms.stream.batch.size:100000}")
    protected int streamLimit;
    protected final ObjectMapper objectMapper = new ObjectMapper();

    private final NodeDAO nodeDAO;
    private final NodeIndexDAO nodeIndexDAO;
    private final CommitDAO commitDAO;
    private final BranchDAO branchDAO;
    private final ObjectFactory<FederatedNodeGetDomain> nodeGetDomainFactory;
    private final ObjectFactory<FederatedNodeChangeDomain> nodeChangeDomainObjectFactory;

    public FederatedNodePersistence(NodeDAO nodeDAO, NodeIndexDAO nodeIndexDAO, CommitDAO commitDAO,
            BranchDAO branchDAO, ObjectFactory<FederatedNodeGetDomain> nodeGetDomainFactory,
            ObjectFactory<FederatedNodeChangeDomain> nodeChangeDomainObjectFactory,
            CommitPersistence commitPersistence) {
        this.nodeDAO = nodeDAO;
        this.nodeIndexDAO = nodeIndexDAO;
        this.commitDAO = commitDAO;
        this.branchDAO = branchDAO;
        this.nodeGetDomainFactory = nodeGetDomainFactory;
        this.nodeChangeDomainObjectFactory = nodeChangeDomainObjectFactory;
        this.commitPersistence = commitPersistence;
    }

    public FederatedNodeGetDomain getNodeGetDomain() {
        return nodeGetDomainFactory.getObject();
    }

    public FederatedNodeChangeDomain getNodeChangeDomain() {
        return nodeChangeDomainObjectFactory.getObject();
    }


    @Override
    public NodeChangeInfo prepareChange(CommitJson commitJson, boolean overwrite, boolean preserveTimestamps) {
        return getNodeChangeDomain().initInfo(commitJson, overwrite, preserveTimestamps);
    }

    @Override
    public NodeChangeInfo prepareAddsUpdates(NodeChangeInfo nodeChangeInfo, Collection<ElementJson> elements) {
        ContextHolder.setContext(nodeChangeInfo.getCommitJson().getProjectId(), nodeChangeInfo.getCommitJson().getRefId());
        primeNodeChangeInfo(nodeChangeInfo, elements);
        return getNodeChangeDomain().processPostJson(nodeChangeInfo, elements);
    }

    @Override
    public NodeChangeInfo prepareDeletes(NodeChangeInfo info, Collection<ElementJson> jsons) {
        if(!(info instanceof  FederatedNodeChangeInfo)) {
            throw new InternalErrorException("Invalid NodeChangeInfo type presented to NodeFederatedDAO");
        }
        ContextHolder.setContext(info.getCommitJson().getProjectId(), info.getCommitJson().getRefId());
        primeNodeChangeInfo(info, jsons);
        return getNodeChangeDomain().processDeleteJson(info, jsons);
    }

    protected void primeNodeChangeInfo(NodeChangeInfo nodeChangeInfo, Collection<ElementJson> transactedElements) {
        getNodeChangeDomain().primeNodeChangeInfo(nodeChangeInfo, transactedElements);
    }

    @Override
    public NodeGetInfo findById(String projectId, String refId, String commitId, String elementId) {
        ContextHolder.setContext(projectId, refId);
        List<ElementJson> elements = new ArrayList<>();
        elements.add(new ElementJson().setId(elementId));
        return getNodeGetDomain().processGetJson(elements, commitId);
    }

    @Override
    public List<ElementJson> findAllByNodeType(String projectId, String refId, String commitId, int nodeType) {
        ContextHolder.setContext(projectId, refId);
        Optional<Branch> branch = branchDAO.findByBranchId(refId);
        validateBranch(branch);
        List<Node> nodes;
        Optional<Commit> latestCommit = commitDAO.findLatestByRef(branch.get());
        if (commitId != null && !commitId.isEmpty() && !commitId.equals(latestCommit.<Object>map(Commit::getCommitId).orElse(null))) {
            validateCommit(commitDAO.findByCommitId(commitId));
            nodes = nodeDAO.findAllByNodeType(nodeType);
        } else {
            nodes = nodeDAO.findAllByDeletedAndNodeType(false, nodeType);
        }
        return new ArrayList<>(getNodeGetDomain().processGetJsonFromNodes(nodes, commitId).getActiveElementMap().values());
    }

    
    @Override
    public NodeGetInfo findAll(String projectId, String refId, String commitId, List<ElementJson> elements) {
        ContextHolder.setContext(projectId, refId);
        return getNodeGetDomain().processGetJson(elements, commitId);
    }

    @Override
    public List<ElementJson> findAll(String projectId, String refId, String commitId) {
        ContextHolder.setContext(projectId, refId);
        Optional<Branch> branch = branchDAO.findByBranchId(refId);
        validateBranch(branch);
        List<Node> nodes;
        Optional<Commit> latestCommit = commitDAO.findLatestByRef(branch.get());
        if (commitId != null && !commitId.isEmpty() && !commitId.equals(latestCommit.<Object>map(Commit::getCommitId).orElse(null))) {
            validateCommit(commitDAO.findByCommitId(commitId));
            nodes = nodeDAO.findAll();
        } else {
            nodes = nodeDAO.findAllByDeleted(false);
        }
        return new ArrayList<>(getNodeGetDomain().processGetJsonFromNodes(nodes, commitId).getActiveElementMap().values());
    }


    @Override
    public void streamAllAtCommit(String projectId, String refId, String commitId, OutputStream stream, String separator) {
        ContextHolder.setContext(projectId, refId);
        List<Node> nodes;
        Optional<Branch> branch = branchDAO.findByBranchId(refId);
        validateBranch(branch);
        Optional<Commit> latestCommit = commitDAO.findLatestByRef(branch.get());
        if (commitId != null && !commitId.isEmpty() && !commitId.equals(latestCommit.<Object>map(Commit::getCommitId).orElse(null))) {
            validateCommit(commitDAO.findByCommitId(commitId));
            nodes = nodeDAO.findAll();
        } else {
            nodes = nodeDAO.findAllByDeleted(false);
        }

        AtomicInteger counter = new AtomicInteger();
        batches(nodes, streamLimit).forEach(ns -> {
            try {
                if (counter.get() == 0) {
                    counter.getAndIncrement();
                } else {
                    stream.write(separator.getBytes(StandardCharsets.UTF_8));
                }
                Collection<ElementJson> result = getNodeGetDomain().processGetJsonFromNodes(ns, commitId)
                    .getActiveElementMap().values();
                stream.write(result.stream().map(this::toJson).collect(Collectors.joining(separator))
                    .getBytes(StandardCharsets.UTF_8));
            } catch (IOException ioe) {
                logger.error("Error writing to stream", ioe);
            }
        });
    }


    @Override
    public void branchElements(RefJson parentBranch, CommitJson parentCommit, RefJson targetBranch) {
        if (parentBranch != null && parentCommit != null && targetBranch != null) {
            String projectId = parentBranch.getProjectId();
            ContextHolder.setContext(projectId, parentBranch.getId());
            Set<String> docIds = new HashSet<>();
            for (Node n : nodeDAO.findAllByDeleted(false)) {
                docIds.add(n.getDocId());
            }
            ContextHolder.setContext(projectId, targetBranch.getId());
            nodeIndexDAO.addToRef(docIds);
        } else {
            throw new InternalErrorException("Error committing transaction");
        }
    }

    protected static <T> Stream<List<T>> batches(List<T> source, int length) {
        return IntStream.iterate(0, i -> i < source.size(), i -> i + length)
            .mapToObj(i -> source.subList(i, Math.min(i + length, source.size())));
    }

    protected String toJson(ElementJson elementJson) {
        try {
            return objectMapper.writeValueAsString(elementJson);
        } catch (JsonProcessingException e) {
            logger.error("Error in toJson: ", e);
        }
        return "";
    }

    @Override
    public FederatedNodeChangeInfo commitChanges(NodeChangeInfo info) {
        if(!(info instanceof FederatedNodeChangeInfo)) {
            throw new InternalErrorException("Invalid NodeChangeInfo type presented to NodeFederatedDAO");
        }
        //TODO: Test rollback on IndexDAO failure
        //TODO: move transaction stuff out into transaction service
        TransactionDefinition def = new DefaultTransactionDefinition();
        TransactionStatus status = nodeDAO.getTransactionManager().getTransaction(def);

        FederatedNodeChangeInfo federatedInfo = (FederatedNodeChangeInfo) info;
        Map<String, Node> nodes = federatedInfo.getToSaveNodeMap();
        Map<String, ElementJson> json = federatedInfo.getUpdatedMap();
        CommitJson cmjs = federatedInfo.getCommitJson();
        Instant now = federatedInfo.getInstant();
        if (!nodes.isEmpty()) {
            try {
                nodeDAO.saveAll(new ArrayList<>(nodes.values()));
                if (json != null && !json.isEmpty()) {
                    nodeIndexDAO.indexAll(json.values());
                }
                nodeIndexDAO.removeFromRef(federatedInfo.getOldDocIds());
                commitPersistence.save(cmjs,now);

                nodeDAO.getTransactionManager().commit(status);
            } catch (Exception e) {
                logger.error("commitChanges error: ", e);
                nodeDAO.getTransactionManager().rollback(status);
                throw new InternalErrorException("Error committing transaction");
            }
        }
        return federatedInfo;
    }

    private void validateBranch(Optional<Branch> branch) {
        if (!branch.isPresent()) {
            throw new InternalErrorException("Cannot find branch");
        }
    }

    private void validateCommit(Optional<Commit> commit) {
        if (!commit.isPresent()) {
            throw new BadRequestException("commit id is invalid");
        }
    }
}
