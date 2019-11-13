package org.openmbee.sdvc.webhooks.json;

import org.openmbee.sdvc.json.BaseJson;

public class WebhookJson extends BaseJson<WebhookJson> {

    public static final String URI = "uri";

    public String getUri() {
        return (String) this.get(URI);
    }

    public WebhookJson setUri(String uri) {
        this.put(URI, uri);
        return this;
    }
}
