package org.openmbee.sdvc.crud.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openmbee.sdvc.crud.repositories.commit.CommitDAOImpl;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class BaseController {

    protected CommitDAOImpl commitRepository;

    protected static final String ORGANIZATION_KEY = "orgs";
    protected static final String PROJECT_KEY = "projects";
    protected static final String BRANCH_KEY = "refs";
    protected final Logger logger = LogManager.getLogger(getClass());
    protected final ObjectMapper om = new ObjectMapper();

    @Autowired
    public void setCommitRepository(CommitDAOImpl commitRepository) {
        this.commitRepository = commitRepository;
    }
}
