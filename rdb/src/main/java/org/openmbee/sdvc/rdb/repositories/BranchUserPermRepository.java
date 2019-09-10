package org.openmbee.sdvc.rdb.repositories;

import java.util.List;
import java.util.Optional;
import org.openmbee.sdvc.data.domains.global.Branch;
import org.openmbee.sdvc.data.domains.global.BranchUserPerm;
import org.openmbee.sdvc.data.domains.global.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BranchUserPermRepository extends JpaRepository<BranchUserPerm, Long> {

    List<BranchUserPerm> findAllByBranch(Branch b);

    List<BranchUserPerm> findAllByBranchAndInherited(Branch b, boolean inherited);

    List<BranchUserPerm> findAllByBranchAndUser(Branch b, User u);

    List<BranchUserPerm> findAllByBranchAndUser_Username(Branch b, String u);

    Optional<BranchUserPerm> findByBranchAndUserAndInherited(Branch b, User u, boolean inherited);

    Optional<BranchUserPerm> findByBranchAndUser_UsernameAndInherited(Branch b, String u, boolean inherited);
}
