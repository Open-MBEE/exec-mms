package org.openmbee.sdvc.rdb.repositories;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import org.openmbee.sdvc.core.dao.ProjectDAO;
import org.openmbee.sdvc.core.exceptions.InternalErrorException;
import org.openmbee.sdvc.data.domains.global.Project;
import org.openmbee.sdvc.rdb.config.DatabaseDefinitionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class ProjectDAOImpl implements ProjectDAO {

    private ProjectRepository projectRepository;

    private DatabaseDefinitionService projectOperations;

    @Autowired
    public void setProjectRepository(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    @Autowired
    public void setDatabaseDefinitionService(DatabaseDefinitionService projectOperations) {
        this.projectOperations = projectOperations;
    }

    @Override
    public Optional<Project> findByProjectId(String id) {
        return projectRepository.findByProjectId(id);
    }

    @Override
    public Optional<Project> findByProjectName(String name) {
        return projectRepository.findByProjectName(name);
    }

    @Override
    public Project save(Project proj) {
        if (proj.getId() == null) {
            try {
                projectOperations.createProjectDatabase(proj);
            } catch (SQLException ex) {
                //TODO db already exists, attempt to delete db?
                throw new InternalErrorException(ex);
            }
        }
        return projectRepository.save(proj);
    }

    @Override
    public void delete(Project p) {
        //TODO delete db
        projectRepository.delete(p);
    }

    @Override
    public List<Project> findAll() {
        return projectRepository.findAll();
    }
}
