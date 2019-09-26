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

    Set<OrgGroupPerm> findAllByOrganizationAndGroup_NameIn(Organization b, Set<String> group);

    Optional<OrgGroupPerm> findByOrganizationAndGroup_Name(Organization b, String group);

    Optional<OrgGroupPerm> findByOrganization_OrganizationIdAndGroup_Name(String orgId, String group);

    List<OrgGroupPerm> findAllByOrganizationAndRole(Organization org, Role r);

    List<OrgGroupPerm> findAllByOrganizationAndRole_Name(Organization org, String r);

    List<OrgGroupPerm> findAllByOrganization_OrganizationIdAndRole_Name(String orgId, String role);

}
