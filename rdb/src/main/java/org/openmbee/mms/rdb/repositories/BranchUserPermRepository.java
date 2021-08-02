package org.openmbee.mms.rdb.repositories;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.openmbee.mms.data.domains.global.Branch;
import org.openmbee.mms.data.domains.global.BranchUserPerm;
import org.openmbee.mms.data.domains.global.Role;
import org.openmbee.mms.data.domains.global.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BranchUserPermRepository extends JpaRepository<BranchUserPerm, Long> {

    Set<BranchUserPerm> findAllByBranch(Branch b);

    Set<BranchUserPerm> findAllByBranchAndInherited(Branch b, boolean inherited);

    Optional<BranchUserPerm> findByBranchAndUserAndInheritedIsFalse(Branch b, User u);

    List<BranchUserPerm> findAllByUser_UsernameAndBranch_Project_ProjectId(String user, String projectId);

    boolean existsByBranchAndUser_UsernameAndRoleIn(Branch b, String user, Set<Role> roles);

    void deleteByBranchAndUser_UsernameInAndInheritedIsFalse(Branch b, Set<String> users);

    void deleteByBranchAndInherited(Branch b, boolean inherited);
}
