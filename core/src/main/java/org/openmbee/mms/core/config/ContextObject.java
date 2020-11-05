package org.openmbee.mms.core.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ContextObject {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    public static final String DEFAULT_PROJECT = "DEFAULT";
    public static final String MASTER_BRANCH = "master";

    private String projectId = DEFAULT_PROJECT;
    private String branchId = MASTER_BRANCH;

    public ContextObject() {
        logger.debug("Context project set to " + projectId);
        logger.debug("Context branch set to " + branchId);
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
            logger.debug("Context project set to " + projectId);
        }
    }

    public String getBranchId() {
        return branchId;
    }

    public void setBranchId(String branchId) {
        if (branchId != null) {
            this.branchId = branchId;
            logger.debug("Context branch set to " + branchId);
        }
    }
}
