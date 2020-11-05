package org.openmbee.mms.rdb.repositories;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.openmbee.mms.data.domains.global.Group;
import org.openmbee.mms.data.domains.global.Project;
import org.openmbee.mms.data.domains.global.ProjectGroupPerm;
import org.openmbee.mms.data.domains.global.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectGroupPermRepository extends JpaRepository<ProjectGroupPerm, Long> {

    List<ProjectGroupPerm> findAllByProject(Project proj);

    List<ProjectGroupPerm> findAllByProjectAndInherited(Project proj, boolean inherited);

    List<ProjectGroupPerm> findAllByProject_ProjectId(String projectId);

    Optional<ProjectGroupPerm> findByProjectAndGroupAndInheritedIsFalse(Project proj, Group group);

    List<ProjectGroupPerm> findAllByProjectAndRole_Name(Project proj, String r);

    boolean existsByProjectAndGroup_NameInAndRoleIn(Project proj, Set<String> groups, Set<Role> roles);

    void deleteByProjectAndGroup_NameInAndInheritedIsFalse(Project proj, Set<String> groups);

    void deleteByProjectAndInherited(Project proj, boolean inherited);

}
