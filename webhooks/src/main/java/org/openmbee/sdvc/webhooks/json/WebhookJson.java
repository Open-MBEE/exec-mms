package org.openmbee.sdvc.webhooks.json;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import org.openmbee.sdvc.json.BaseJson;

@JsonIgnoreProperties({BaseJson.COMMITID, BaseJson.REFID, BaseJson.DOCID, BaseJson.NAME, "empty"})
@Schema(requiredProperties = {WebhookJson.URL})
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
