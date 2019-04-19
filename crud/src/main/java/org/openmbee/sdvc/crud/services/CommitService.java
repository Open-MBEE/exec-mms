package org.openmbee.sdvc.crud.services;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.openmbee.sdvc.crud.config.DbContextHolder;
import org.openmbee.sdvc.crud.repositories.branch.BranchDAO;
import org.openmbee.sdvc.data.domains.Branch;
import org.openmbee.sdvc.json.CommitJson;
import org.openmbee.sdvc.crud.controllers.commits.CommitsRequest;
import org.openmbee.sdvc.crud.controllers.commits.CommitsResponse;
import org.openmbee.sdvc.data.domains.Commit;
import org.openmbee.sdvc.crud.repositories.commit.CommitDAO;
import org.openmbee.sdvc.crud.repositories.commit.CommitIndexDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CommitService {

    public static SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
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
        DbContextHolder.setContext(projectId, refId);
        int limit = 0;
        Instant timestamp = null;
        if (params.containsKey("limit")) {
            limit = Integer.parseInt(params.get("limit"));
        }
        if (params.containsKey("maxTimestamp")) {
            try {
                timestamp = df.parse(params.get("maxTimestamp")).toInstant();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        List<Commit> commits = findByRefAndTimestampAndLimit(refId, timestamp, limit);
        CommitsResponse res = new CommitsResponse();
        List<CommitJson> resJson = new ArrayList<>();
        for (Commit c : commits) {
            CommitJson json = new CommitJson();
            json.setCreated(c.getTimestamp().toString());
            json.setCreator(c.getCreator());
            json.setId(c.getIndexId());
            json.setComment(c.getComment());
            resJson.add(json);
        }
        res.setCommits(resJson);
        return res;
    }

    public CommitsResponse getCommit(String projectId, String commitId) {
        DbContextHolder.setContext(projectId);
        CommitsResponse res = new CommitsResponse();
        try {
            Optional<CommitJson> commit = commitIndex.findById(commitId);
            if (commit.isPresent()) {
                res.getCommits().add(commit.get());
            } else {
                res.setCode(404);
            }
        } catch (Exception e) {
            e.printStackTrace();
            res.setCode(500);
        }
        return res;
    }

    public CommitsResponse getElementCommits(String projectId, String refId, String elementId,
        Map<String, String> params) {
        DbContextHolder.setContext(projectId);
        CommitsResponse res = new CommitsResponse();
        try {
            List<Commit> refCommits = findByRefAndTimestampAndLimit(refId, null, 0);
            Set<String> commitIds = new HashSet<>();
            for (Commit commit: refCommits) {
                commitIds.add(commit.getIndexId());
            }
            res.getCommits().addAll(commitIndex.elementHistory(elementId, commitIds));
        } catch (Exception e) {
            e.printStackTrace();
            res.setCode(500);
        }
        return res;
    }

    public CommitsResponse getCommits(String projectId, CommitsRequest req) {
        DbContextHolder.setContext(projectId);
        Set<String> ids = new HashSet<>();
        for (CommitJson j : req.getCommits()) {
            ids.add(j.getId());
        }
        CommitsResponse res = new CommitsResponse();
        try {
            res.getCommits().addAll(commitIndex.findAllById(ids));
        } catch (Exception e) {
            e.printStackTrace();
            res.setCode(500);
        }
        return res;
    }

    public Optional<Commit> findLatestByRef(String refId) {
        return findByRefAndTimestamp(refId, null);
    }

    public Optional<Commit> findByRefAndTimestamp(String refId, Instant timestamp) {
        List<Commit> res = findByRefAndTimestampAndLimit(refId, timestamp, 1);
        if (!res.isEmpty()) {
            return Optional.of(res.get(0));
        }
        return Optional.empty();
    }

    public List<Commit> findByRefAndTimestampAndLimit(String refId, Instant timestamp, int limit) {
        List<Commit> commits = new ArrayList<>();
        String currentRef = refId;
        Long currentCid = 0L;
        while (currentRef != null && (commits.size() < limit || limit == 0)) {
            int currentLimit = limit == 0 ? 0 : limit - commits.size();
            List<Commit> next = commitRepository.findByRefAndLimit(currentRef, currentCid, timestamp, currentLimit);
            commits.addAll(next);
            Optional<Branch> ref = branchRepository.findByBranchId(currentRef);
            if (ref.isPresent()) {
                currentRef = ref.get().getParentRefId();
                currentCid = ref.get().getParentCommit();
            }

            if (currentRef != null && currentRef.equals("")) {
                currentRef = null;
            }
        }
        return commits;
    }
}
