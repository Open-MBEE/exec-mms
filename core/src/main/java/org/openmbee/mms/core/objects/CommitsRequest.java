package org.openmbee.mms.core.objects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

import org.openmbee.mms.json.CommitJson;

@JsonIgnoreProperties({"source", "comment"})
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
