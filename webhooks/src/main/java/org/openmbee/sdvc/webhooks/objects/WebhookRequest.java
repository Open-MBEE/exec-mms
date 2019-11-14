package org.openmbee.sdvc.webhooks.objects;

import org.openmbee.sdvc.webhooks.json.WebhookJson;

import java.util.List;

public class WebhookRequest {
    private List<WebhookJson> webhooks;

    public List<WebhookJson> getWebhooks() {
        return webhooks;
    }

    public void setWebhooks(List<WebhookJson> webhooks) {
        this.webhooks = webhooks;
    }
}
