package org.openmbee.sdvc.rdb.repositories;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.openmbee.sdvc.data.domains.global.Project;
import org.openmbee.sdvc.data.domains.global.ProjectUserPerm;
import org.openmbee.sdvc.data.domains.global.Role;
import org.openmbee.sdvc.data.domains.global.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectUserPermRepository extends JpaRepository<ProjectUserPerm, Long> {

    List<ProjectUserPerm> findAllByProject(Project proj);

    List<ProjectUserPerm> findAllByProjectAndInherited(Project proj, boolean inherited);

    List<ProjectUserPerm> findAllByProject_ProjectId(String projectId);

    List<ProjectUserPerm> findAllByProject_ProjectIdAndInherited(String projectId, boolean inherited);

    Set<ProjectUserPerm> findAllByProjectAndUser(Project proj, User user);

    Optional<ProjectUserPerm> findByProjectAndUserAndInherited(Project proj, User user, boolean inherited);

    Set<ProjectUserPerm> findAllByProjectAndUser_Username(Project proj, String User);

    Optional<ProjectUserPerm> findByProjectAndUser_UsernameAndInheritedIsFalse(Project proj, String User);

    List<ProjectUserPerm> findByProjectAndUser_UsernameAndInherited(Project proj, String User, boolean inherited);

    Set<ProjectUserPerm> findAllByProject_ProjectIdAndUser_Username(String projectId, String User);

    Optional<ProjectUserPerm> findByProject_ProjectIdAndUser_UsernameAndInheritedIsFalse(String projectId, String User);

    List<ProjectUserPerm> findByProject_ProjectIdAndUser_UsernameAndInherited(String projectId, String User, boolean inherited);

    List<ProjectUserPerm> findAllByProjectAndRole(Project proj, Role r);

    List<ProjectUserPerm> findAllByProjectAndRoleAndInherited(Project proj, Role r, boolean inherited);

    List<ProjectUserPerm> findAllByProjectAndRole_Name(Project proj, String r);

    List<ProjectUserPerm> findAllByProjectAndRole_NameAndInherited(Project proj, String r, boolean inherited);

    List<ProjectUserPerm> findAllByProject_ProjectIdAndRole_Name(String projectId, String role);

    List<ProjectUserPerm> findAllByProject_ProjectIdAndRole_NameAndInherited(String projectId, String role, boolean inherited);
}
