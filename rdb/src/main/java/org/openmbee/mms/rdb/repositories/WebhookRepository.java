package org.openmbee.mms.rdb.repositories;

import org.openmbee.mms.data.domains.global.Webhook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WebhookRepository extends JpaRepository<Webhook, Long> {

    List<Webhook> findAllByProject_ProjectId(String projectId);

    Optional<Webhook> findByProject_ProjectIdAndUrl(String projectId, String url);

}