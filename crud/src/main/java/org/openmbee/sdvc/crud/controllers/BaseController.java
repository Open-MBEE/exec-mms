package org.openmbee.sdvc.crud.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openmbee.sdvc.crud.repositories.commit.CommitDAOImpl;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class BaseController {

    protected final Logger logger = LogManager.getLogger(getClass());
    protected final ObjectMapper om = new ObjectMapper();
}
