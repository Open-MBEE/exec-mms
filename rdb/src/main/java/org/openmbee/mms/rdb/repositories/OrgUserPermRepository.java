package org.openmbee.mms.rdb.repositories;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.openmbee.mms.data.domains.global.OrgUserPerm;
import org.openmbee.mms.data.domains.global.Organization;
import org.openmbee.mms.data.domains.global.Role;
import org.openmbee.mms.data.domains.global.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrgUserPermRepository extends JpaRepository<OrgUserPerm, Long> {

    List<OrgUserPerm> findAllByOrganization(Organization org);

    List<OrgUserPerm> findAllByOrganization_OrganizationId(String orgId);

    Optional<OrgUserPerm> findByOrganizationAndUser(Organization b, User u);

    List<OrgUserPerm> findAllByOrganizationAndRole_Name(Organization org, String r);

    boolean existsByOrganizationAndUser_UsernameAndRoleIn(Organization org, String user, Set<Role> roles);

    void deleteByOrganizationAndUser_UsernameIn(Organization org, Set<String> users);

    void deleteByOrganization(Organization org);
}
