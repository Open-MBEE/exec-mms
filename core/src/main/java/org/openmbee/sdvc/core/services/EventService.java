package org.openmbee.sdvc.core.services;

import org.openmbee.sdvc.core.objects.BaseEvent;

public interface EventService {
    boolean publish(BaseEvent event);
}
