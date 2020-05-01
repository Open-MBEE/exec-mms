package org.openmbee.sdvc.core.config;

public class ContextObject {

    public static final String DEFAULT_PROJECT = "DEFAULT";
    public static final String MASTER_BRANCH = "master";

    private String projectId = DEFAULT_PROJECT;
    private String branchId = MASTER_BRANCH;
    private String db = "sdr";
    private String index = "mms";
    private String dbTableSuffix = "";

    public ContextObject() {
    }

    public ContextObject(String projectId) {
        this.setProjectId(projectId);
        this.projectIdUpdated();
    }

    public ContextObject(String projectId, String branchId) {
        this.setProjectId(projectId);
        this.setBranchId(branchId);
        this.projectIdUpdated();
        this.branchIdUpdated();
    }

    public String getKey() {
        return this.getProjectId();
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        if (projectId != null) {
            this.projectId = projectId;
            this.projectIdUpdated();
        }
    }

    public String getBranchId() {
        return branchId;
    }

    public void setBranchId(String branchId) {
        if (branchId != null) {
            this.branchId = branchId;
            this.branchIdUpdated();
        }
    }

    public String getDb() {
        return this.db;
    }

    public String getIndex() {
        return this.index;
    }

    public String getDbTableSuffix() {
        return this.dbTableSuffix;
    }

    private void projectIdUpdated() {
        this.db = "_" + projectId;
        this.index = this.projectId.toLowerCase();
    }

    private void branchIdUpdated() {
        this.dbTableSuffix = this.branchId.equals(MASTER_BRANCH) ? "" : this.branchId;
    }
}
