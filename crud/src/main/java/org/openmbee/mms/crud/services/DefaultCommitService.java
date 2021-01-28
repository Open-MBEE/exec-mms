package org.openmbee.mms.crud.services;

import java.text.ParseException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.openmbee.mms.core.config.ContextHolder;
import org.openmbee.mms.core.config.Formats;
import org.openmbee.mms.core.exceptions.BadRequestException;
import org.openmbee.mms.core.exceptions.InternalErrorException;
import org.openmbee.mms.core.exceptions.NotFoundException;
import org.openmbee.mms.core.dao.BranchDAO;
import org.openmbee.mms.core.services.CommitService;
import org.openmbee.mms.data.domains.scoped.Branch;
import org.openmbee.mms.json.CommitJson;
import org.openmbee.mms.core.objects.CommitsRequest;
import org.openmbee.mms.core.objects.CommitsResponse;
import org.openmbee.mms.data.domains.scoped.Commit;
import org.openmbee.mms.core.dao.CommitDAO;
import org.openmbee.mms.core.dao.CommitIndexDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("defaultCommitService")
public class DefaultCommitService implements CommitService {
    protected CommitDAO commitRepository;
    private CommitIndexDAO commitIndex;
    private BranchDAO branchRepository;

    @Autowired
    public void setCommitDao(CommitDAO commitDao) {
        this.commitRepository = commitDao;
    }

    @Autowired
    public void setBranchRepository(BranchDAO branchRepository) {
        this.branchRepository = branchRepository;
    }

    @Autowired
    public void setCommitElasticDao(CommitIndexDAO commitElasticRepository) {
        this.commitIndex = commitElasticRepository;
    }

    @Override
    public CommitsResponse getRefCommits(String projectId, String refId, Map<String, String> params) {
        ContextHolder.setContext(projectId, refId);
        int limit = 0;
        Instant timestamp = null;
        if (params.containsKey("limit")) {
            limit = Integer.parseInt(params.get("limit"));
        }
        if (params.containsKey("maxTimestamp")) {
            try {
                timestamp = Formats.SDF.parse(params.get("maxTimestamp")).toInstant();
            } catch (ParseException e) {
                e.printStackTrace();
                throw new BadRequestException("maxTimestamp parse error, use " +
                    Formats.FORMATTER.format(Instant.now()) + " as example");
            }
        }
        Optional<Branch> ref = branchRepository.findByBranchId(refId);
        List<CommitJson> resJson = new ArrayList<>();
        CommitsResponse res = new CommitsResponse();

        int finalLimit = limit;
        Instant finalTimestamp = timestamp;

        ref.ifPresentOrElse(branch -> {
            List<Commit> commits = commitRepository.findByRefAndTimestampAndLimit(branch, finalTimestamp, finalLimit);
            for (Commit c : commits) {
                CommitJson json = new CommitJson();
                json.setCreated(Formats.FORMATTER.format(c.getTimestamp()));
                json.setCreator(c.getCreator());
                json.setId(c.getCommitId());
                json.setComment(c.getComment());
                json.setRefId(c.getBranchId());
                json.setProjectId(projectId);
                resJson.add(json);
            }
        }, () -> {
            throw new NotFoundException("Branch not found");
        });

        res.setCommits(resJson);
        return res;
    }

    @Override
    public CommitsResponse getCommit(String projectId, String commitId) {
        ContextHolder.setContext(projectId);
        CommitsResponse res = new CommitsResponse();

        Optional<CommitJson> commit = commitIndex.findById(commitId);
        if (commit.isPresent()) {
            res.getCommits().add(commit.get());
        } else {
            throw new NotFoundException("Commit not found");
        }
        return res;
    }

    @Override
    public CommitsResponse getElementCommits(String projectId, String refId, String elementId, Map<String, String> params) {
        ContextHolder.setContext(projectId);
        CommitsResponse res = new CommitsResponse();
        Optional<Branch> ref = branchRepository.findByBranchId(refId);
        if (!ref.isPresent()) {
            throw new NotFoundException("Branch not found");
        }
        List<Commit> refCommits = commitRepository.findByRefAndTimestampAndLimit(ref.get(), null, 0);
        Set<String> commitIds = new HashSet<>();
        for (Commit commit: refCommits) {
            commitIds.add(commit.getCommitId());
        }
        res.getCommits().addAll(commitIndex.elementHistory(elementId, commitIds));
        return res;
    }

    @Override
    public CommitsResponse getCommits(String projectId, CommitsRequest req) {
        ContextHolder.setContext(projectId);
        Set<String> ids = new HashSet<>();
        for (CommitJson j : req.getCommits()) {
            ids.add(j.getId());
        }
        CommitsResponse res = new CommitsResponse();
        try {
            res.getCommits().addAll(commitIndex.findAllById(ids));
        } catch (Exception e) {
            e.printStackTrace();
            throw new InternalErrorException(e);
        }
        return res;
    }

    @Override
    public boolean isProjectNew(String projectId) {
        ContextHolder.setContext(projectId);
        List<Commit> commits = commitRepository.findAll();
        return commits == null || commits.isEmpty();
    }
}
