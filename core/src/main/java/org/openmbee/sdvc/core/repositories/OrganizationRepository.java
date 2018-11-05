package org.openmbee.sdvc.core.repositories;

import org.openmbee.sdvc.core.domains.Organization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrganizationRepository extends JpaRepository<Organization, String> {

    Organization findByOrganizationId(String id);

    Organization findByOrganizationName(String name);

}
