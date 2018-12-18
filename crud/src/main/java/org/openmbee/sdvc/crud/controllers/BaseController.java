package org.openmbee.sdvc.crud.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openmbee.sdvc.crud.services.ServiceFactory;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class BaseController {

    protected final Logger logger = LogManager.getLogger(getClass());

    protected ObjectMapper om;

    protected ServiceFactory serviceFactory;

    @Autowired
    public void setServiceFactory(ServiceFactory serviceFactory) {
        this.serviceFactory = serviceFactory;
    }

    @Autowired
    public void setObjectMapper(ObjectMapper om) {
        this.om = om;
    }
}
