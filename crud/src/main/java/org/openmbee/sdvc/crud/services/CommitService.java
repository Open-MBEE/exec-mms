package org.openmbee.sdvc.crud.services;

import java.text.ParseException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.openmbee.sdvc.core.config.ContextHolder;
import org.openmbee.sdvc.core.config.Formats;
import org.openmbee.sdvc.core.exceptions.BadRequestException;
import org.openmbee.sdvc.core.exceptions.InternalErrorException;
import org.openmbee.sdvc.core.exceptions.NotFoundException;
import org.openmbee.sdvc.core.dao.BranchDAO;
import org.openmbee.sdvc.data.domains.scoped.Branch;
import org.openmbee.sdvc.json.CommitJson;
import org.openmbee.sdvc.core.objects.CommitsRequest;
import org.openmbee.sdvc.core.objects.CommitsResponse;
import org.openmbee.sdvc.data.domains.scoped.Commit;
import org.openmbee.sdvc.core.dao.CommitDAO;
import org.openmbee.sdvc.core.dao.CommitIndexDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CommitService {

    private CommitDAO commitRepository;
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

    public CommitsResponse getRefCommits(String projectId, String refId,
        Map<String, String> params) {
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
                json.setId(c.getDocId());
                json.setComment(c.getComment());
                resJson.add(json);
            }
        }, () -> {
            throw new NotFoundException("Branch not found");
        });

        res.setCommits(resJson);
        return res;
    }

    public CommitsResponse getCommit(String projectId, String commitId) {
        ContextHolder.setContext(projectId);
        CommitsResponse res = new CommitsResponse();
        try {
            Optional<CommitJson> commit = commitIndex.findById(commitId);
            if (commit.isPresent()) {
                res.getCommits().add(commit.get());
            } else {
                throw new NotFoundException(res);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new InternalErrorException(e);
        }
        return res;
    }

    public CommitsResponse getElementCommits(String projectId, String refId, String elementId,
        Map<String, String> params) {
        ContextHolder.setContext(projectId);
        CommitsResponse res = new CommitsResponse();
        try {
            Optional<Branch> ref = branchRepository.findByBranchId(refId);
            if (!ref.isPresent()) {
                throw new NotFoundException("Branch not found");
            }
            List<Commit> refCommits = commitRepository.findByRefAndTimestampAndLimit(ref.get(), null, 0);
            Set<String> commitIds = new HashSet<>();
            for (Commit commit: refCommits) {
                commitIds.add(commit.getDocId());
            }
            res.getCommits().addAll(commitIndex.elementHistory(elementId, commitIds));
        } catch (Exception e) {
            e.printStackTrace();
            throw new InternalErrorException(e);
        }
        return res;
    }

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
}
