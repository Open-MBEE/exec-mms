package org.openmbee.sdvc.core.services;

import org.openmbee.sdvc.core.objects.EventObject;

public interface EventService {
    boolean publish(EventObject event);
}
