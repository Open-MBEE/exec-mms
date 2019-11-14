package org.openmbee.sdvc.webhooks.objects;

import org.openmbee.sdvc.core.config.Constants;
import org.openmbee.sdvc.core.objects.BaseResponse;
import org.openmbee.sdvc.webhooks.json.WebhookJson;

import java.util.ArrayList;
import java.util.List;

public class WebhookResponse extends BaseResponse<WebhookResponse> {

    public WebhookResponse() {
        this.put(Constants.WEBHOOK_KEY, new ArrayList<WebhookJson>());
    }

    public List<WebhookJson> getWebhooks() {
        return (List<WebhookJson>) this.get(Constants.WEBHOOK_KEY);
    }

    public WebhookResponse setWebhooks(List<WebhookJson> webhooks) {
        this.put(Constants.WEBHOOK_KEY, webhooks);
        return this;
    }

}
