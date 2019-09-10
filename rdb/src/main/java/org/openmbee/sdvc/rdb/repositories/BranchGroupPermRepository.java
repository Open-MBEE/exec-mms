package org.openmbee.sdvc.rdb.repositories;

import java.util.List;
import java.util.Optional;
import org.openmbee.sdvc.data.domains.global.Branch;
import org.openmbee.sdvc.data.domains.global.BranchGroupPerm;
import org.openmbee.sdvc.data.domains.global.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BranchGroupPermRepository extends JpaRepository<BranchGroupPerm, Long> {

    List<BranchGroupPerm> findAllByBranch(Branch b);

    List<BranchGroupPerm> findAllByBranchAndInherited(Branch b, boolean inherited);

    List<BranchGroupPerm> findAllByBranchAndGroup(Branch b, Group g);

    List<BranchGroupPerm> findAllByBranchAndGroup_Name(Branch b, String name);

    Optional<BranchGroupPerm> findByBranchAndGroupAndInherited(Branch b, Group g, boolean inherited);

    Optional<BranchGroupPerm> findByBranchAndGroup_NameAndInherited(Branch b, String g, boolean inherited);

}
