package org.openmbee.mms.core.services;

import org.openmbee.mms.core.objects.EventObject;

public interface EventService {
    boolean publish(EventObject event);
}
