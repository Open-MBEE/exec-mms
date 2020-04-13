package org.openmbee.sdvc.core.objects;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

import org.openmbee.sdvc.json.CommitJson;

public class CommitsRequest extends BaseRequest {

    @Schema(required = true)
    private List<CommitJson> commits;

    public List<CommitJson> getCommits() {
        return commits;
    }

    public void setCommits(List<CommitJson> commits) {
        this.commits = commits;
    }

}
