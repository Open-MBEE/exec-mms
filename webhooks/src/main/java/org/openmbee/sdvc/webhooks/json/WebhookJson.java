package org.openmbee.sdvc.webhooks.json;

import org.openmbee.sdvc.json.BaseJson;

public class WebhookJson extends BaseJson<WebhookJson> {

    public static final String PROJECTID = "projectId";
    public static final String URI = "uri";

    public String getProjectid() {
        return (String) this.get(PROJECTID);
    }

    public WebhookJson setProjectid(String projectId) {
        this.put(PROJECTID, projectId);
        return this;
    }

    public String getUri() {
        return (String) this.get(URI);
    }

    public WebhookJson setUri(String uri) {
        this.put(URI, uri);
        return this;
    }
}
