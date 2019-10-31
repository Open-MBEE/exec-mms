package org.openmbee.sdvc.core.dao;

import org.openmbee.sdvc.core.objects.BaseEvent;
import org.openmbee.sdvc.data.domains.global.Event;

import java.util.List;
import java.util.Optional;

public interface EventDAO {

    Event save(Event event);

    Optional<Event> findByBranchId(String eventid);

    List<Event> findAll();

    List<Event> findAllByProject_ProjectId(String projectId);

    void delete(Event event);
}
