package org.openmbee.sdvc.rdb.repositories;

import org.openmbee.sdvc.data.domains.global.TWCIntegration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TWCIntegrationRepository extends JpaRepository<TWCIntegration, Long> {
    Optional<TWCIntegration> findTWCIntegrationByProjectId(String id);
    Optional<TWCIntegration> findTWCIntegrationByProjectName(String name);
}
