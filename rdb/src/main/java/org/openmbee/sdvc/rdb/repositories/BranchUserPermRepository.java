package org.openmbee.sdvc.rdb.repositories;

import org.openmbee.sdvc.data.domains.global.BranchUserPerm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BranchUserPermRepository extends JpaRepository<BranchUserPerm, Long> {

}
