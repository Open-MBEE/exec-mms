package org.openmbee.sdvc.core.objects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openmbee.sdvc.core.config.Constants;

public abstract class BaseResponse<T> extends HashMap<String, Object> {

    protected final Logger logger = LogManager.getLogger(getClass());

    @SuppressWarnings("unchecked")
    public List<String> getMessages() {
        return (List<String>) this.get(Constants.MESSAGES);
    }

    public T setMessages(List<String> messages) {
        this.put(Constants.MESSAGES, messages);
        return (T) this;
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

    public List<Map> getRejected() {
        return (List<Map>) this.get(Constants.REJECTED);
    }

    public T setRejected(List<Map> rejected) {
        this.put(Constants.REJECTED, rejected);
        return (T) this;
    }
}
