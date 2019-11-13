package org.openmbee.sdvc.core.components;

import org.openmbee.sdvc.core.objects.EventObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class EventPublisher {
    private ApplicationEventPublisher applicationEventPublisher;

    @Autowired
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    public void publish(final EventObject event) {
        applicationEventPublisher.publishEvent(event);
    }

    public void publish(final String event, final Object payload) {
        applicationEventPublisher.publishEvent(event);
    }
}
