package org.openmbee.sdvc.twc.metadata;

public class TwcMetadata {
    private String host;
    private String workspaceId;
    private String resourceId;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getWorkspaceId() {
        return workspaceId;
    }

    public void setWorkspaceId(String workspaceId) {
        this.workspaceId = workspaceId;
    }

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }


    public boolean isComplete() {
        return host != null && !host.isEmpty()
            && workspaceId != null && !workspaceId.isEmpty()
            && resourceId != null && !resourceId.isEmpty();
    }
}
