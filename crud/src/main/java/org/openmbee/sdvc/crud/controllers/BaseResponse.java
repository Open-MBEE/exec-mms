package org.openmbee.sdvc.crud.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class BaseResponse extends HashMap<String, Object> {

    protected final Logger logger = LogManager.getLogger(getClass());

    public List<String> getMessages() {
        return (List<String>) this.get(Constants.MESSAGES);
    }

    public void setMessages(List<String> messages) {
        this.put(Constants.MESSAGES, messages);
    }

    public void addMessage(String message) {
        if (this.get(Constants.MESSAGES) == null) {
            this.setMessages(new ArrayList<>());
        }
        this.getMessages().add(message);
    }

    public int getCode() {
        return (int) this.get(Constants.CODE);
    }

    public void setCode(int code) {
        this.put(Constants.CODE, code);
    }
}
