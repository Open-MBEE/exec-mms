package org.openmbee.sdvc.core.config;

import org.openmbee.sdvc.core.objects.BaseEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class EventPublisher {
    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    public void publishEvent(final String message) {
        BaseEvent customSpringEvent = new BaseEvent(this, message);
        applicationEventPublisher.publishEvent(customSpringEvent);
    }
}
