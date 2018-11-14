package org.openmbee.sdvc.crud.controllers;

import java.util.HashMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class BaseResponse extends HashMap<String, Object> {

    protected final Logger logger = LogManager.getLogger(getClass());
}
