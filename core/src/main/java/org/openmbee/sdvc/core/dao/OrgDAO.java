package org.openmbee.sdvc.core.dao;

import java.util.List;
import java.util.Optional;
import org.openmbee.sdvc.data.domains.global.Organization;

public interface OrgDAO {

    Optional<Organization> findByOrganizationId(String id);

    Optional<Organization> findByOrganizationName(String name);

    Organization save(Organization org);

    void delete(Organization org);

    List<Organization> findAll();
}
