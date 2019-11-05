package org.openmbee.sdvc.webhooks.components;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openmbee.sdvc.core.dao.WebhookDAO;
import org.openmbee.sdvc.data.domains.global.Webhook;
import org.openmbee.sdvc.core.objects.EventObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

@Component
public class EventListener implements ApplicationListener<EventObject> {

    protected final Logger logger = LogManager.getLogger(getClass());

    private WebhookDAO eventRepository;

    @Autowired
    public void setEventRepository(WebhookDAO eventRepository) {
        this.eventRepository = eventRepository;
    }

    @Override
    public void onApplicationEvent(EventObject eventObject) {
        RestTemplate restTemplate = new RestTemplate();
        List<Webhook> webhooks = eventRepository.findAllByProject_ProjectId(eventObject.getProjectId());

        for (Webhook webhook : webhooks) {
            try {
                URI uri = new URI(webhook.getUri());

                HttpHeaders headers = new HttpHeaders();
                headers.set("Content-Type", "application/json");
                HttpEntity<Object> request = new HttpEntity<>(eventObject.getPayload(), headers);

                ResponseEntity<String> result = restTemplate.postForEntity(uri, request, String.class);
                if (result.getStatusCodeValue() == 200) {
                    logger.info("Sent event to " + webhook.getUri() + " with payload: " + eventObject.getPayload());
                }
            } catch (URISyntaxException se) {
                // Do nothing; Nowhere to post;
                logger.error("Error in web hook: ", se);
                return;
            }
            logger.info("Sent event to " + webhook.getUri() + " with payload: " + eventObject.getPayload());
        }
    }
}
