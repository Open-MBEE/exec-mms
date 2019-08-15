package org.openmbee.sdvc.core.objects;

import java.util.ArrayList;
import java.util.List;

import org.openmbee.sdvc.core.config.Constants;
import org.openmbee.sdvc.json.CommitJson;

public class CommitsResponse extends BaseResponse<CommitsResponse> {

    public CommitsResponse() {
        this.put(Constants.COMMIT_KEY, new ArrayList<CommitJson>());
    }

    public List<CommitJson> getCommits() {
        return (List<CommitJson>) this.get(Constants.COMMIT_KEY);
    }

    public void setCommits(List<CommitJson> commits) {
        this.put(Constants.COMMIT_KEY, commits);
    }
}
