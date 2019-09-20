package org.openmbee.sdvc.rdb.repositories;

import java.util.Optional;
import java.util.Set;

import org.openmbee.sdvc.data.domains.global.Branch;
import org.openmbee.sdvc.data.domains.global.BranchUserPerm;
import org.openmbee.sdvc.data.domains.global.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BranchUserPermRepository extends JpaRepository<BranchUserPerm, Long> {

    Set<BranchUserPerm> findAllByBranch(Branch b);

    Set<BranchUserPerm> findAllByBranchAndInherited(Branch b, boolean inherited);

    Set<BranchUserPerm> findAllByBranchAndUser(Branch b, User u);

    Set<BranchUserPerm> findAllByBranchAndUser_Username(Branch b, String u);

    Set<BranchUserPerm> findAllByBranch_BranchIdAndUser_Username(String b, String u);

    Set<BranchUserPerm> findByBranchAndUserAndInherited(Branch b, User u, boolean inherited);

    Optional<BranchUserPerm> findByBranchAndUserAndInheritedIsFalse(Branch b, User u);

    Optional<BranchUserPerm> findByBranchAndUser_UsernameAndInherited(Branch b, String u, boolean inherited);
}
