package org.openmbee.mms.twc.metadata;

import org.openmbee.mms.twc.constants.TwcConstants;

import java.util.HashMap;
import java.util.Map;

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

    public Map<String, String> toMap() {
        Map<String, String> result = new HashMap<>();
        result.put(TwcConstants.HOST_KEY, getHost());
        result.put(TwcConstants.WORKSPACE_ID_KEY, getWorkspaceId());
        result.put(TwcConstants.RESOURCE_ID_KEY, getResourceId());
        return result;
    }
}
