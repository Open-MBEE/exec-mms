package org.openmbee.sdvc.crud.config;

public class ContextObject {

    public static final String DEFAULT_PROJECT = "DEFAULT";
    public static final String MASTER_BRANCH = "master";

    private String projectId = DEFAULT_PROJECT;
    private String branchId = MASTER_BRANCH;

    public ContextObject() {
    }

    public ContextObject(String projectId) {
        this.setProjectId(projectId);
    }

    public ContextObject(String projectId, String branchId) {
        this.setProjectId(projectId);
        this.setBranchId(branchId);
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
        }
    }

    public String getBranchId() {
        return branchId;
    }

    public void setBranchId(String branchId) {
        if (branchId != null) {
            this.branchId = branchId;
        }
    }
}
