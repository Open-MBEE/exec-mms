package org.openmbee.mms.core.objects;

import io.swagger.v3.oas.annotations.media.Schema;
import org.openmbee.mms.json.ElementJson;

import java.util.List;

public class ElementsCommitResponse extends ElementsResponse {

    @Schema(nullable = true)
    private String commitId;

    private List<ElementJson> deleted;
    public String getCommitId() {
        return commitId;
    }

    public void setCommitId(String commitId) {
        this.commitId = commitId;
    }

    public List<ElementJson> getDeleted() {
        return deleted;
    }

    public ElementsResponse setDeleted(List<ElementJson> deleted) {
        this.deleted = deleted;
        return this;
    }
}
