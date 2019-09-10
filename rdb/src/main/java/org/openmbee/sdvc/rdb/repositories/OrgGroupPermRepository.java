package org.openmbee.sdvc.rdb.repositories;

import org.openmbee.sdvc.data.domains.global.OrgGroupPerm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrgGroupPermRepository extends JpaRepository<OrgGroupPerm, Long> {

}
