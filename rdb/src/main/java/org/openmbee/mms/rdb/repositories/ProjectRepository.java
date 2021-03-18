package org.openmbee.mms.rdb.repositories;

import java.util.List;
import java.util.Optional;

import org.openmbee.mms.data.domains.global.Organization;
import org.openmbee.mms.data.domains.global.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;
import javax.persistence.LockModeType;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {

    Optional<Project> findByProjectId(String id);

    Optional<Project> findByProjectName(String name);

    List<Project> findAllByOrganizationOrganizationId(String id);

    @Override
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    <S extends Project> S save(S entity);

    @Override
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    void delete(Project entity);
}
