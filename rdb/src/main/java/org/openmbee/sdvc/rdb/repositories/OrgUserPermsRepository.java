package org.openmbee.sdvc.rdb.repositories;

import org.openmbee.sdvc.data.domains.global.OrgUserPerm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrgUserPermsRepository extends JpaRepository<OrgUserPerm, Long> {

}
