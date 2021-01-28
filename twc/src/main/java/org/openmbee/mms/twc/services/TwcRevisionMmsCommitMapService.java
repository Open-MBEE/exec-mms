package org.openmbee.mms.twc.services;

import org.openmbee.mms.core.config.ContextHolder;
import org.openmbee.mms.core.config.Formats;
import org.openmbee.mms.core.dao.BranchDAO;
import org.openmbee.mms.core.exceptions.InternalErrorException;
import org.openmbee.mms.core.exceptions.NotFoundException;
import org.openmbee.mms.core.objects.CommitsResponse;
import org.openmbee.mms.core.services.NodeService;
import org.openmbee.mms.crud.services.DefaultNodeService;
import org.openmbee.mms.data.domains.scoped.Branch;
import org.openmbee.mms.data.domains.scoped.Commit;
import org.openmbee.mms.json.CommitJson;
import org.openmbee.mms.twc.constants.TwcConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.*;

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
        if (revisionId == null || revisionId.isEmpty()) {
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
            logger.error("Error occurred while associating commit ID and Twc Revision: " + exception.getMessage());
            throw new InternalErrorException(exception);
        }
        return commitsResponse;
    }

    /**
     * Method to get the twc revision list with project and branch id
     * @param projectId
     * @param refId
     * @param reverseOrder
     * @param limit
     * @return
     */
    public List<CommitJson> getTwcRevisionList(String projectId, String refId, Boolean reverseOrder, Integer limit) {
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
                commitIds.add(commit.getCommitId());
            });
            List<CommitJson> commitJsonList = commitIndex.findAllById(commitIds);
            if (null != commitJsonList && commitJsonList.size() > 0) {
                commitJsonList.stream().forEach(commitJsonData -> {
                    if (commitJsonData.containsKey(TwcConstants.TWCREVISIONID)) {
                        commits.add(commitJsonData);
                    }
                });
            }
            commits.sort(new CommitsComparator(reverseOrder));
            if(limit != null && limit > 0) {
                return commits.subList(0, Math.min(limit, commits.size()));
            }
            return commits;
        } catch (Exception exception) {
            logger.error("Error occurred while getting Twc Revision: " + exception.getMessage());
            throw new InternalErrorException(exception);
        }
    }

    private class CommitsComparator implements Comparator<CommitJson> {

        private boolean ascending = true;

        public CommitsComparator(Boolean reverseOrder) {
            if(reverseOrder != null && reverseOrder) {
                ascending = false;
            }
        }

        @Override
        public int compare(CommitJson o, CommitJson t1) {
            try {
                Date d1 = Formats.SDF.parse((String) o.get(CommitJson.CREATED));
                Date d2 = Formats.SDF.parse((String) t1.get(CommitJson.CREATED));
                return ascending ? d1.compareTo(d2) : d2.compareTo(d1);
            } catch (ParseException e) {
                logger.error("Error parsing commit dates: " + e.getMessage());
                throw new InternalErrorException("Invalid commit created dates.");
            }
        }
    }
}
