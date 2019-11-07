package org.openmbee.sdvc.webhooks.json;

import org.openmbee.sdvc.json.BaseJson;

public class WebhookJson extends BaseJson<WebhookJson> {
    private String uri;

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }
}
