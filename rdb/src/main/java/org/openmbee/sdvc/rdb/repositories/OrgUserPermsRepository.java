package org.openmbee.sdvc.rdb.repositories;

import java.util.List;
import java.util.Optional;
import org.openmbee.sdvc.data.domains.global.OrgUserPerm;
import org.openmbee.sdvc.data.domains.global.Organization;
import org.openmbee.sdvc.data.domains.global.Role;
import org.openmbee.sdvc.data.domains.global.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrgUserPermsRepository extends JpaRepository<OrgUserPerm, Long> {

    List<OrgUserPerm> findAllByOrganization(Organization org);

    List<OrgUserPerm> findAllByOrganization_OrganizationId(String orgId);

    Optional<OrgUserPerm> findByOrganizationAndUser(Organization b, User u);

    Optional<OrgUserPerm> findAllByOrganizationAndUser_Username(Organization b, String u);

    Optional<OrgUserPerm> findAllByOrganization_OrganizationIdAndUser_Username(String orgId, String u);

    List<OrgUserPerm> findAllByOrganizationAndRole(Organization org, Role r);

    List<OrgUserPerm> findAllByOrganizationAndRole_Name(Organization org, String r);

    List<OrgUserPerm> findAllByOrganization_OrganizationIdAndRole_Name(String orgId, String role);

}
