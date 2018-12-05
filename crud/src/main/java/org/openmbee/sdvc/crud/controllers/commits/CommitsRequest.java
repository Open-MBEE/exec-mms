package org.openmbee.sdvc.crud.controllers.commits;

import java.util.List;
import org.openmbee.sdvc.crud.controllers.BaseRequest;

public class CommitsRequest extends BaseRequest {

    private List<CommitJson> commits;

    public List<CommitJson> getCommits() {
        return commits;
    }

    public void setCommits(List<CommitJson> commits) {
        this.commits = commits;
    }

}
