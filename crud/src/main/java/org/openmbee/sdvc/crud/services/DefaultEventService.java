package org.openmbee.sdvc.crud.services;

import org.openmbee.sdvc.core.objects.BaseEvent;
import org.openmbee.sdvc.core.services.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
public class DefaultEventService implements EventService {

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    public boolean publish(BaseEvent event) {
        applicationEventPublisher.publishEvent(event);
        return true;
    }
}
