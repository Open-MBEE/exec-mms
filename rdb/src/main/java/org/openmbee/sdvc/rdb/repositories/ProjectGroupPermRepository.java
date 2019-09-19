package org.openmbee.sdvc.rdb.repositories;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.openmbee.sdvc.data.domains.global.Group;
import org.openmbee.sdvc.data.domains.global.Project;
import org.openmbee.sdvc.data.domains.global.ProjectGroupPerm;
import org.openmbee.sdvc.data.domains.global.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectGroupPermRepository extends JpaRepository<ProjectGroupPerm, Long> {

    List<ProjectGroupPerm> findAllByProject(Project proj);

    List<ProjectGroupPerm> findAllByProjectAndInherited(Project proj, boolean inherited);

    List<ProjectGroupPerm> findAllByProject_ProjectId(String projectId);

    List<ProjectGroupPerm> findAllByProject_ProjectIdAndInherited(String projectId, boolean inherited);

    Optional<ProjectGroupPerm> findByProjectAndGroup(Project proj, Group group);

    Set<ProjectGroupPerm> findAllByProjectAndGroup(Project proj, Set<Group> group);

    Optional<ProjectGroupPerm> findByProjectAndGroupAndInherited(Project proj, Group group, boolean inherited);

    List<ProjectGroupPerm> findAllByProjectAndGroup_Name(Project proj, String group);

    Optional<ProjectGroupPerm> findByProjectAndGroup_NameAndInheritedIsFalse(Project proj, String group);

    List<ProjectGroupPerm> findByProjectAndGroup_NameAndInherited(Project proj, String group, boolean inherited);

    List<ProjectGroupPerm> findAllByProject_ProjectIdAndGroup_Name(String projectId, String group);

    Optional<ProjectGroupPerm> findByProject_ProjectIdAndGroup_NameAndInheritedIsFalse(String projectId, String group);

    List<ProjectGroupPerm> findByProject_ProjectIdAndGroup_NameAndInherited(String projectId, String group, boolean inherited);

    List<ProjectGroupPerm> findAllByProjectAndRole(Project proj, Role r);

    List<ProjectGroupPerm> findAllByProjectAndRoleAndInherited(Project proj, Role r, boolean inherited);

    List<ProjectGroupPerm> findAllByProjectAndRole_Name(Project proj, String r);

    List<ProjectGroupPerm> findAllByProjectAndRole_NameAndInherited(Project proj, String r, boolean inherited);

    List<ProjectGroupPerm> findAllByProject_ProjectIdAndRole_Name(String projectId, String role);

    List<ProjectGroupPerm> findAllByProject_ProjectIdAndRole_NameAndInherited(String projectId, String role, boolean inherited);

}
