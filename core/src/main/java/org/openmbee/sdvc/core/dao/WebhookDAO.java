package org.openmbee.sdvc.core.dao;

import org.openmbee.sdvc.data.domains.global.Webhook;

import java.util.List;
import java.util.Optional;

public interface WebhookDAO {

    Webhook save(Webhook webhook);

    List<Webhook> findAll();

    Optional<Webhook> findById(Long id);

    List<Webhook> findAllByProject_ProjectId(String projectId);

    Optional<Webhook> findByProject_ProjectIdAndUri(String projectId, String uri);

    void delete(Webhook webhook);
}
