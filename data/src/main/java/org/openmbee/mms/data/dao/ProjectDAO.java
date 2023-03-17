package org.openmbee.mms.data.dao;

import java.util.List;
import java.util.Optional;
import org.openmbee.mms.data.domains.global.Project;

import javax.transaction.Transactional;

public interface ProjectDAO {

    Optional<Project> findByProjectId(String id);

    Optional<Project> findByProjectName(String name);

    public List<Project> findAllByOrgId(String id);

    @Transactional(Transactional.TxType.REQUIRES_NEW)
    Project save(Project p);

    @Transactional(Transactional.TxType.REQUIRES_NEW)
    void delete(String projectId);

    List<Project> findAll();
}
