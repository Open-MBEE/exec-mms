package org.openmbee.mms.json;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.AccessMode;

@JsonIgnoreProperties({"empty", BaseJson.COMMITID, "tag"})
@Schema(name = "Ref", requiredProperties = {BaseJson.TYPE, BaseJson.NAME})
public class RefJson extends BaseJson<RefJson> {

    public static final String PARENT_REF_ID = "parentRefId";
    public static final String PARENT_COMMIT_ID = "parentCommitId";
    public static final String STATUS = "status";
    public static final String DESCRIPTION = "description";
    public static final String TAG = "Tag";
    public static final String DELETED = "deleted";

    @Schema(defaultValue = "master")
    public String getParentRefId() {
        return (String) this.get(PARENT_REF_ID);
    }

    public RefJson setParentRefId(String parentRefId) {
        this.put(PARENT_REF_ID, parentRefId);
        return this;
    }

    public String getParentCommitId() {
        return (String) this.get(PARENT_COMMIT_ID);
    }

    public RefJson setParentCommitId(String parentCommitId) {
        this.put(PARENT_COMMIT_ID, parentCommitId);
        return this;
    }

    @Schema(accessMode = AccessMode.READ_ONLY)
    public String getStatus() {
        return (String) this.get(STATUS);
    }

    public RefJson setStatus(String status) {
        this.put(STATUS, status);
        return this;
    }

    @JsonProperty("type")
    public RefType getRefType() {
        return RefType.valueOf((String) this.get(TYPE));
    }

    @JsonProperty("type")
    public RefJson setRefType(RefType type) {
        this.put(TYPE, type.name());
        return this;
    }

    public boolean isTag() {
        return TAG.equals(getType());
    }

    public String getDescription() {
        return (String) this.getOrDefault(DESCRIPTION, "");
    }

    public RefJson setDeleted(boolean deleted) {
        this.put(DELETED, deleted);
        return this;
    }

    @Schema(accessMode = AccessMode.READ_ONLY)
    public boolean isDeleted() {
        return (Boolean) this.getOrDefault(DELETED, false);
    }

}
