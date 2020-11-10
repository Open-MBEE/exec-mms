package org.openmbee.mms.core.objects;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BaseResponse<T> {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Schema(nullable = true)
    private List<String> messages = new ArrayList<>();

    @Schema(nullable = true)
    private List<Rejection> rejected = new ArrayList<>();

    @SuppressWarnings("unchecked")
    public List<String> getMessages() {
        return messages;
    }

    public T setMessages(List<String> messages) {
        this.messages = messages;
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public T addMessage(String message) {
        if (this.messages == null) {
            this.setMessages(new ArrayList<>());
        }
        this.getMessages().add(message);
        return (T) this;
    }

    public List<Rejection> getRejected() {
        return rejected;
    }

    public T setRejected(List<Rejection> rejected) {
        this.rejected = rejected;
        return (T) this;
    }

    public T addRejection(Rejection rejection) {
        if (this.rejected == null) {
            this.rejected = new ArrayList<>();
        }
        this.rejected.add(rejection);
        return (T) this;
    }
}
