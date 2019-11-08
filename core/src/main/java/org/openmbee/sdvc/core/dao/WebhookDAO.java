package org.openmbee.sdvc.core.dao;

import org.openmbee.sdvc.data.domains.global.Webhook;

import java.util.List;

public interface WebhookDAO {

    Webhook save(Webhook webhook);

    List<Webhook> findAll();

    List<Webhook> findAllByProject_ProjectId(String projectId);

    void delete(Webhook webhook);
}
