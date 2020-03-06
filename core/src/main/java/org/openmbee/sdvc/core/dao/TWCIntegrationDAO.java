package org.openmbee.sdvc.core.dao;

import org.openmbee.sdvc.data.domains.global.TWCIntegration;

import java.util.List;
import java.util.Optional;

public interface TWCIntegrationDAO {
    Optional<TWCIntegration> findTWCIntegrationByProjectId(String id);
    Optional<TWCIntegration> findTWCIntegrationByProjectName(String name);
    TWCIntegration save(TWCIntegration integration);
    void delete(TWCIntegration integration);
    List<TWCIntegration> findAll();
}
