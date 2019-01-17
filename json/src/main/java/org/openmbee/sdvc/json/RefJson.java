package org.openmbee.sdvc.json;

public class RefJson extends BaseJson {

    public static final String PARENT_REF_ID = "parentRefId";
    public static final String PARENT_COMMIT_ID = "parentCommitId";
    public static final String STATUS = "status";
    public static final String TYPE = "type";
    public static final String DESCRIPTION = "description";

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

    public RefJson setParentCommitId(int parentCommitId) {
        this.put(PARENT_COMMIT_ID, parentCommitId);
        return this;
    }

    public String getStatus() {
        return (String) this.get(STATUS);
    }

    public RefJson setStatus(String status) {
        this.put(STATUS, status);
        return this;
    }

    public String getType() {
        return (String) this.get(TYPE);
    }

    public RefJson setType(String type) {
        this.put(TYPE, type);
        return this;
    }

    public boolean isTag() {
        return "Tag".equals(getType());
    }

    public String getDescription() {
        return (String) this.getOrDefault(DESCRIPTION, "");
    }

}
