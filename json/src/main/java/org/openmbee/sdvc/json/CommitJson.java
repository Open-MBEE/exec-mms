package org.openmbee.sdvc.json;

import java.util.List;
import java.util.Map;

public class CommitJson extends BaseJson<CommitJson> {

    public static final String COMMENT = "comment";
    public static final String ADDED = "added";
    public static final String DELETED = "deleted";
    public static final String UPDATED = "updated";
    public static final String PREVIOUS = "previousIndexId";
    public static final String TYPE = "type";
    public static final String SOURCE = "source";

    public String getComment() {
        return (String) this.get(COMMENT);
    }

    public CommitJson setComment(String comment) {
        this.put(COMMENT, comment);
        return this;
    }

    public List<Map<String, Object>> getAdded() {
        return (List<Map<String, Object>>) this.get(ADDED);
    }

    public CommitJson setAdded(List<Map<String, Object>> added) {
        this.put(ADDED, added);
        return this;
    }

    public List<Map<String, Object>> getDeleted() {
        return (List<Map<String, Object>>) this.get(DELETED);
    }

    public CommitJson setDeleted(List<Map<String, Object>> deleted) {
        this.put(DELETED, deleted);
        return this;
    }

    public List<Map<String, Object>> getUpdated() {
        return (List<Map<String, Object>>) this.get(UPDATED);
    }

    public CommitJson setUpdated(List<Map<String, Object>> updated) {
        this.put(UPDATED, updated);
        return this;
    }

    public String getSource() {
        return (String) this.get(SOURCE);
    }

    public CommitJson setSource(String source) {
        this.put(SOURCE, source);
        return this;
    }
}
