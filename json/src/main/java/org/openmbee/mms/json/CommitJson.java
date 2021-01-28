package org.openmbee.mms.json;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.AccessMode;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@JsonIgnoreProperties({"empty", BaseJson.NAME, BaseJson.MODIFIER, BaseJson.MODIFIED, BaseJson.TYPE})
@Schema(name = "Commit", requiredProperties = {BaseJson.ID})
public class CommitJson extends BaseJson<CommitJson> {

    public static final String COMMENT = "comment";
    public static final String ADDED = "added";
    public static final String DELETED = "deleted";
    public static final String UPDATED = "updated";
    public static final String SOURCE = "source";

    public static CommitJson copy(CommitJson copy, CommitJson original) {
        if (copy.getAdded() == null) {
            copy.setAdded(new ArrayList<>());
        }
        if (copy.getUpdated() == null) {
            copy.setUpdated(new ArrayList<>());
        }
        if (copy.getDeleted() == null) {
            copy.setDeleted(new ArrayList<>());
        }

        if (original.getAdded() != null) {
            copy.getAdded().addAll(original.getAdded());
            original.remove(CommitJson.ADDED);
        }
        if (original.getUpdated() != null) {
            copy.getUpdated().addAll(original.getUpdated());
            original.remove(CommitJson.UPDATED);
        }
        if (original.getDeleted() != null) {
            copy.getDeleted().addAll(original.getDeleted());
            original.remove(CommitJson.DELETED);
        }

        copy.putAll(original);
        return copy;
    }

    @Schema(accessMode = AccessMode.READ_ONLY)
    public String getComment() {
        return (String) this.get(COMMENT);
    }

    public CommitJson setComment(String comment) {
        this.put(COMMENT, comment);
        return this;
    }

    @ArraySchema(schema = @Schema(accessMode = AccessMode.READ_ONLY, implementation = ElementVersion.class))
    public List<Map<String, Object>> getAdded() {
        return (List<Map<String, Object>>) this.get(ADDED);
    }

    public CommitJson setAdded(List<Map<String, Object>> added) {
        this.put(ADDED, added);
        return this;
    }

    @ArraySchema(schema = @Schema(accessMode = AccessMode.READ_ONLY, implementation = ElementVersion.class))
    public List<Map<String, Object>> getDeleted() {
        return (List<Map<String, Object>>) this.get(DELETED);
    }

    public CommitJson setDeleted(List<Map<String, Object>> deleted) {
        this.put(DELETED, deleted);
        return this;
    }

    @ArraySchema(schema = @Schema(accessMode = AccessMode.READ_ONLY, implementation = ElementVersion.class))
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
