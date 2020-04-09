package org.openmbee.sdvc.json;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.AccessMode;

@JsonIgnoreProperties({"empty", BaseJson.REFID, BaseJson.COMMITID, BaseJson.PROJECTID,
    BaseJson.CREATOR, BaseJson.CREATED, BaseJson.MODIFIER, BaseJson.MODIFIED, BaseJson.NAME})
public class CommitUpdatedJson extends BaseJson<CommitUpdatedJson> {

    @Schema(accessMode = AccessMode.READ_ONLY)
    public String getPreviousDocId() {
        return (String) this.get(CommitJson.PREVIOUS);
    }

    public CommitUpdatedJson setPreviousDocId(String previousDocId) {
        this.put(CommitJson.PREVIOUS, previousDocId);
        return this;
    }
}