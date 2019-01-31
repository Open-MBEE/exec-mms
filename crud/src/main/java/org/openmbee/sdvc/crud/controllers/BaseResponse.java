package org.openmbee.sdvc.crud.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class BaseResponse<T> extends HashMap<String, Object> {

    protected final Logger logger = LogManager.getLogger(getClass());

    @SuppressWarnings("unchecked")
    public List<String> getMessages() {
        return (List<String>) this.get(Constants.MESSAGES);
    }

    public void setMessages(List<String> messages) {
        this.put(Constants.MESSAGES, messages);
    }

    @SuppressWarnings("unchecked")
    public T addMessage(String message) {
        if (this.get(Constants.MESSAGES) == null) {
            this.setMessages(new ArrayList<>());
        }
        this.getMessages().add(message);
        return (T) this;
    }

    public int getCode() {
        return (int) this.get(Constants.CODE);
    }

    @SuppressWarnings("unchecked")
    public T setCode(int code) {
        this.put(Constants.CODE, code);
        return (T) this;
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
