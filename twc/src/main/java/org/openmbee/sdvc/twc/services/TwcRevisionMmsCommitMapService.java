package org.openmbee.sdvc.twc.services;

import org.openmbee.sdvc.core.config.ContextHolder;
import org.openmbee.sdvc.core.dao.BranchDAO;
import org.openmbee.sdvc.core.exceptions.InternalErrorException;
import org.openmbee.sdvc.core.exceptions.NotFoundException;
import org.openmbee.sdvc.core.objects.CommitsResponse;
import org.openmbee.sdvc.core.services.NodeService;
import org.openmbee.sdvc.crud.services.DefaultNodeService;
import org.openmbee.sdvc.data.domains.scoped.Branch;
import org.openmbee.sdvc.data.domains.scoped.Commit;
import org.openmbee.sdvc.json.CommitJson;
import org.openmbee.sdvc.twc.constants.TwcConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.ArrayList;

@Service("twcRevisionMmsCommitMapService")
public class TwcRevisionMmsCommitMapService extends DefaultNodeService implements NodeService {
    private BranchDAO branchRepository;

    @Autowired
    public void setBranchRepository(BranchDAO branchRepository) {
        this.branchRepository = branchRepository;
    }

    /**
     * Method to update the twc revision id with commit id
     * @param projectId
     * @param commitId
     * @param revisionId
     * @return
     */
    public CommitsResponse updateTwcRevisionID(String projectId, String commitId, String revisionId) {
        CommitsResponse commitsResponse = new CommitsResponse();
        if (revisionId.isEmpty() || revisionId.isBlank()) {
            return commitsResponse.addMessage("Revision id can not be empty");
        }
        ContextHolder.setContext(projectId);
        Optional<CommitJson> commitJsonDetails = commitIndex.findById(commitId);
        try {
            if (commitJsonDetails.isPresent()) {
                CommitJson commitObj = commitJsonDetails.get();
                commitObj.put(TwcConstants.TWCREVISIONID, revisionId);
                commitsResponse.getCommits().add(this.commitIndex.update(commitObj));
            } else {
                throw new NotFoundException(commitsResponse);
            }
        } catch (Exception exception) {
            logger.error("Error occurred while associating commit ID and Twc Revision", exception.getMessage());
            throw new InternalErrorException(exception);
        }
        return commitsResponse;
    }

    /**
     * Method to get the twc revision list with project and branch id
     * @param projectId
     * @param refId
     * @return
     */
    public List<CommitJson> getTwcRevisionList(String projectId, String refId) {
        List<CommitJson> commits = new ArrayList<>();
        ContextHolder.setContext(projectId);
        Optional<Branch> ref = branchRepository.findByBranchId(refId);
        if (!ref.isPresent()) {
            throw new NotFoundException("Branch not found");
        }
        try {
            List<Commit> refCommits = commitRepository.findByRefAndTimestampAndLimit(ref.get(), null, 0);
            Set<String> commitIds = new HashSet<>();
            refCommits.stream().forEach(commit -> {
                commitIds.add(commit.getDocId());
            });
            List<CommitJson> commitJsonList = commitIndex.findAllById(commitIds);
            if (null != commitJsonList && commitJsonList.size() > 0) {
                commitJsonList.stream().forEach(commitJsonData -> {
                    if (commitJsonData.containsKey(TwcConstants.TWCREVISIONID)) {
                        commits.add(commitJsonData);
                    }
                });
            }
        } catch (Exception exception) {
            logger.error("Error occurred while getting Twc Revision", exception.getMessage());
            throw new InternalErrorException(exception);
        }
        return commits;
    }
}
