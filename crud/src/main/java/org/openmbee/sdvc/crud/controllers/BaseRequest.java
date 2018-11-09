package org.openmbee.sdvc.crud.controllers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;

public abstract class BaseRequest extends HashMap<String, Object> {

    protected final Logger logger = LogManager.getLogger(getClass());

    public String getSource() {
        return (String) this.get("source");
    }
}
