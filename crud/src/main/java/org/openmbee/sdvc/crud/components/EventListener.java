package org.openmbee.sdvc.crud.components;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openmbee.sdvc.core.dao.EventDAO;
import org.openmbee.sdvc.core.objects.BaseEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

@Component
public class EventListener implements ApplicationListener<BaseEvent> {

    protected final Logger logger = LogManager.getLogger(getClass());

    private EventDAO eventRepository;

    @Autowired
    public void setEventRepository(EventDAO eventRepository) {
        this.eventRepository = eventRepository;
    }

    @Override
    public void onApplicationEvent(BaseEvent event) {
        RestTemplate restTemplate = new RestTemplate();
        try {
            URI uri = new URI(event.getUri());

            HttpHeaders headers = new HttpHeaders();
            headers.set("Content-Type", "application/json");
            HttpEntity<Map> request = new HttpEntity<>(event.getPayload(), headers);

            ResponseEntity<String> result = restTemplate.postForEntity(uri, request, String.class);
            if (result.getStatusCodeValue() == 200) {
                logger.info("Sent event to " + event.getUri() + " with payload: " + event.getPayload());
            }
        } catch (URISyntaxException se) {
            // Do nothing; Nowhere to post;
            logger.error("Error in web hook: ", se);
            return;
        }
        logger.info("Sent event to " + event.getUri() + " with payload: " + event.getPayload());
    }
}
