package org.openmbee.sdvc.crud.controllers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;

public abstract class BaseResponse extends HashMap<String, Object> {

    protected final Logger logger = LogManager.getLogger(getClass());
}
