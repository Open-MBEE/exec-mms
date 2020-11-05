package org.openmbee.mms.core.objects;

import org.springframework.context.ApplicationEvent;

import java.util.Map;

public class EventObject extends ApplicationEvent {
    private Object payload;
    private String event;
    private String projectId;
    private String branchId;

    public EventObject(Map<String, Object> context) {
        super(context);
        this.projectId = context.getOrDefault("projectId", null).toString();
        this.branchId = context.getOrDefault("branchId", "master").toString();
        this.event = context.getOrDefault("event", null).toString();
        this.payload = context.getOrDefault("payload", null);
    }

    public static EventObject create(String projectId, String branchId, String event, Object payload) {
        return new EventObject((Map.of("projectId", projectId, "branchId", branchId, "event", event, "payload", payload)));
    }

    public Object getPayload() {
        return payload;
    }

    public String getEvent() {
        return event;
    }

    public String getProjectId() {
        return projectId;
    }

    public String getBranchId() {
        return branchId;
    }
}
