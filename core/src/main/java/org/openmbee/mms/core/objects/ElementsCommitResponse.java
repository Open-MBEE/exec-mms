package org.openmbee.mms.core.objects;

import io.swagger.v3.oas.annotations.media.Schema;

public class ElementsCommitResponse extends ElementsResponse {

    @Schema(nullable = true)
    private String commitId;

    public String getCommitId() {
        return commitId;
    }

    public void setCommitId(String commitId) {
        this.commitId = commitId;
    }
}
