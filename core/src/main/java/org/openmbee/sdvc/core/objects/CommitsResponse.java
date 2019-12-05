package org.openmbee.sdvc.core.objects;

import java.util.ArrayList;
import java.util.List;

import org.openmbee.sdvc.core.config.Constants;
import org.openmbee.sdvc.json.CommitJson;

public class CommitsResponse extends BaseResponse<CommitsResponse> {

    private List<CommitJson> commits;

    public CommitsResponse() {
        this.commits = new ArrayList<>();
    }

    public List<CommitJson> getCommits() {
        return commits;
    }

    public CommitsResponse setCommits(List<CommitJson> commits) {
        this.commits = commits;
        return this;
    }
}
