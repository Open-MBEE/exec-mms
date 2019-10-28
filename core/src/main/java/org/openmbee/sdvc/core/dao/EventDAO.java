package org.openmbee.sdvc.core.dao;

import org.openmbee.sdvc.core.objects.BaseEvent;

import java.util.List;
import java.util.Optional;

public interface EventDAO {

    BaseEvent save(BaseEvent event);

    Optional<BaseEvent> findByBranchId(String eventid);

    List<BaseEvent> findAll();

    List<BaseEvent> findAllByProject_ProjectId(String projectId);

    void delete(BaseEvent event);
}
