package org.openmbee.mms.webhooks.objects;

import io.swagger.v3.oas.annotations.media.Schema;
import org.openmbee.mms.webhooks.json.WebhookJson;

import java.util.List;

public class WebhookRequest {
    private List<WebhookJson> webhooks;

    @Schema(required = true)
    public List<WebhookJson> getWebhooks() {
        return webhooks;
    }

    public void setWebhooks(List<WebhookJson> webhooks) {
        this.webhooks = webhooks;
    }
}
