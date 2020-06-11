package org.openmbee.sdvc.twc.services;

import org.openmbee.sdvc.core.config.ContextHolder;
import org.openmbee.sdvc.core.exceptions.InternalErrorException;
import org.openmbee.sdvc.core.exceptions.NotFoundException;
import org.openmbee.sdvc.core.objects.CommitsResponse;
import org.openmbee.sdvc.core.services.NodeService;
import org.openmbee.sdvc.crud.services.DefaultNodeService;
import org.openmbee.sdvc.json.CommitJson;
import org.openmbee.sdvc.twc.constants.TwcConstants;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service("twcRevisionMmsCommitMapService")
public class TwcRevisionMmsCommitMapService extends DefaultNodeService implements NodeService {
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
}
