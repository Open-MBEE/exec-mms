package org.openmbee.sdvc.webhooks.services;

import org.openmbee.sdvc.core.objects.EventObject;
import org.openmbee.sdvc.core.services.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
public class WebhookEventService implements EventService {

    private ApplicationEventPublisher applicationEventPublisher;

    @Autowired
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    public boolean publish(EventObject event) {
        applicationEventPublisher.publishEvent(event);
        return true;
    }
}
