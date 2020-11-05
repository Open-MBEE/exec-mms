package org.openmbee.mms.rdb.repositories;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.openmbee.mms.data.domains.global.Branch;
import org.openmbee.mms.data.domains.global.BranchGroupPerm;
import org.openmbee.mms.data.domains.global.Group;
import org.openmbee.mms.data.domains.global.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BranchGroupPermRepository extends JpaRepository<BranchGroupPerm, Long> {

    List<BranchGroupPerm> findAllByBranch(Branch b);

    List<BranchGroupPerm> findAllByBranchAndInherited(Branch b, boolean inherited);

    Optional<BranchGroupPerm> findByBranchAndGroupAndInheritedIsFalse(Branch b, Group g);

    boolean existsByBranchAndGroup_NameInAndRoleIn(Branch b, Set<String> groups, Set<Role> roles);

    void deleteByBranchAndGroup_NameInAndInheritedIsFalse(Branch b, Set<String> groups);

    void deleteByBranchAndInherited(Branch b, boolean inherited);

}
