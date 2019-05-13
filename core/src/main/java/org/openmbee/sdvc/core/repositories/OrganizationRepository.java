package org.openmbee.sdvc.core.repositories;

import java.util.Optional;
import org.openmbee.sdvc.data.domains.Organization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrganizationRepository extends JpaRepository<Organization, String> {

    Optional<Organization> findByOrganizationId(String id);

    Optional<Organization> findByOrganizationName(String name);

}
