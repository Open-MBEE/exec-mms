package org.openmbee.sdvc.webhooks.objects;

import org.openmbee.sdvc.core.objects.BaseResponse;
import org.openmbee.sdvc.webhooks.json.WebhookJson;

import java.util.ArrayList;
import java.util.List;

public class WebhookResponse extends BaseResponse<WebhookResponse> {

    private List<WebhookJson> webhooks;

    public WebhookResponse() {
        this.webhooks = new ArrayList<>();
    }

    public List<WebhookJson> getWebhooks() {
        return webhooks;
    }

    public WebhookResponse setWebhooks(List<WebhookJson> webhooks) {
        this.webhooks = webhooks;
        return this;
    }

}
