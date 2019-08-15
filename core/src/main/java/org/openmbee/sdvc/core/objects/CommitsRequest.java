package org.openmbee.sdvc.core.objects;

import java.util.List;

import org.openmbee.sdvc.json.CommitJson;

public class CommitsRequest extends BaseRequest {

    private List<CommitJson> commits;

    public List<CommitJson> getCommits() {
        return commits;
    }

    public void setCommits(List<CommitJson> commits) {
        this.commits = commits;
    }

}
