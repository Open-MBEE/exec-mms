package org.openmbee.sdvc.rdb.repositories;

import org.openmbee.sdvc.data.domains.global.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    List<Event> findAllByProject_ProjectId(String projectId);

    Optional<Event> findAll(String name);

}