package org.openmbee.sdvc.core.objects;

import org.springframework.context.ApplicationEvent;

public class BaseEvent extends ApplicationEvent {
    private String message;

    public BaseEvent(Object source, String message) {
        super(source);
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
