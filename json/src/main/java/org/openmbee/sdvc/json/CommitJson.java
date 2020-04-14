package org.openmbee.sdvc.json;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.AccessMode;
import java.util.List;
import java.util.Map;

@JsonIgnoreProperties({"empty", BaseJson.NAME, BaseJson.MODIFIER, BaseJson.MODIFIED, BaseJson.TYPE})
@Schema(requiredProperties = {BaseJson.ID})
public class CommitJson extends BaseJson<CommitJson> {

    public static final String COMMENT = "comment";
    public static final String ADDED = "added";
    public static final String DELETED = "deleted";
    public static final String UPDATED = "updated";
    public static final String PREVIOUS = "previousDocId";
    public static final String SOURCE = "source";

    @Schema(accessMode = AccessMode.READ_ONLY)
    public String getComment() {
        return (String) this.get(COMMENT);
    }

    public CommitJson setComment(String comment) {
        this.put(COMMENT, comment);
        return this;
    }

    @ArraySchema(schema = @Schema(accessMode = AccessMode.READ_ONLY, implementation = CommitAddedJson.class))
    public List<Map<String, Object>> getAdded() {
        return (List<Map<String, Object>>) this.get(ADDED);
    }

    public CommitJson setAdded(List<Map<String, Object>> added) {
        this.put(ADDED, added);
        return this;
    }

    @ArraySchema(schema = @Schema(accessMode = AccessMode.READ_ONLY, implementation = CommitDeletedJson.class))
    public List<Map<String, Object>> getDeleted() {
        return (List<Map<String, Object>>) this.get(DELETED);
    }

    public CommitJson setDeleted(List<Map<String, Object>> deleted) {
        this.put(DELETED, deleted);
        return this;
    }

    @ArraySchema(schema = @Schema(accessMode = AccessMode.READ_ONLY, implementation = CommitUpdatedJson.class))
    public List<Map<String, Object>> getUpdated() {
        return (List<Map<String, Object>>) this.get(UPDATED);
    }

    public CommitJson setUpdated(List<Map<String, Object>> updated) {
        this.put(UPDATED, updated);
        return this;
    }

    @Schema(accessMode = AccessMode.READ_ONLY)
    public String getSource() {
        return (String) this.get(SOURCE);
    }

    public CommitJson setSource(String source) {
        this.put(SOURCE, source);
        return this;
    }
}
