package org.openmbee.sdvc.rdb.repositories;

import org.openmbee.sdvc.data.domains.global.Branch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BranchRepository extends JpaRepository<Branch, Long> {

}
