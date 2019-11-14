package org.openmbee.sdvc.webhooks.json;

import org.openmbee.sdvc.json.BaseJson;

public class WebhookJson extends BaseJson<WebhookJson> {

    public static final String URL = "url";

    public String getUrl() {
        return (String) this.get(URL);
    }

    public WebhookJson setUrl(String url) {
        this.put(URL, url);
        return this;
    }
}
