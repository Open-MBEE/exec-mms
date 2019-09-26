package org.openmbee.sdvc.rdb.repositories;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.openmbee.sdvc.data.domains.global.Group;
import org.openmbee.sdvc.data.domains.global.OrgGroupPerm;
import org.openmbee.sdvc.data.domains.global.Organization;
import org.openmbee.sdvc.data.domains.global.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrgGroupPermRepository extends JpaRepository<OrgGroupPerm, Long> {

    List<OrgGroupPerm> findAllByOrganization(Organization org);

    List<OrgGroupPerm> findAllByOrganization_OrganizationId(String orgId);

    Optional<OrgGroupPerm> findByOrganizationAndGroup(Organization b, Group group);

    List<OrgGroupPerm> findAllByOrganizationAndRole_Name(Organization org, String r);

    boolean existsByOrganizationAndGroup_NameInAndRoleIn(Organization org, Set<String> user, Set<Role> roles);

    void deleteByOrganizationAndGroup_NameIn(Organization org, Set<String> groups);

}
