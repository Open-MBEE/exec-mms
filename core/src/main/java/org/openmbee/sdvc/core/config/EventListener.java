package org.openmbee.sdvc.core.config;

import org.openmbee.sdvc.core.objects.BaseEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class EventListener implements ApplicationListener<BaseEvent> {
    @Override
    public void onApplicationEvent(BaseEvent event) {
        System.out.println("Received event - " + event.getMessage());
    }
}
