package org.openmbee.sdvc.core.objects;

import org.springframework.context.ApplicationEvent;

import java.util.Map;

public class BaseEvent extends ApplicationEvent {
    private Object payload;
    private String event;

    public BaseEvent(Object source, String event, Object payload) {
        super(source);
        this.event = event;
        this.payload = payload;
    }

    public Object getPayload() {
        return payload;
    }

    public String getEvent() {
        return event;
    }
}
