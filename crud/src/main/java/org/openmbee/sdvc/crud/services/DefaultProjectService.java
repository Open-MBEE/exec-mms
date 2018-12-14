package org.openmbee.sdvc.crud.services;

import java.sql.SQLException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openmbee.sdvc.core.domains.Project;
import org.openmbee.sdvc.core.repositories.ProjectRepository;
import org.openmbee.sdvc.json.ProjectJson;
import org.openmbee.sdvc.crud.controllers.projects.ProjectsResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("defaultProjectService")
public class DefaultProjectService implements ProjectService {

    protected final Logger logger = LogManager.getLogger(getClass());
    protected ProjectRepository projectRepository;
    protected DatabaseDefinitionService projectOperations;

    @Autowired
    public void setProjectRepository(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    @Autowired
    public void setDatabaseDefinitionService(DatabaseDefinitionService projectOperations) {
        this.projectOperations = projectOperations;
    }

    public ProjectJson create(ProjectJson project) {
        Project proj = new Project();
        proj.setProjectId(project.getId());
        proj.setProjectName(project.getName());
        Project saved = projectRepository.save(proj);

        try {
            if (projectOperations.createProjectDatabase(proj)) {
                //TODO create elastic indexes and mappings
                return project;
            }
        } catch (SQLException sqlException) {
            // Database already exists
            return project;
        } catch (Exception e) {
            projectRepository.delete(saved);
            logger.error("Couldn't create database: {}", project.getProjectId());
            logger.error(e);
        }
        return null; //throw exception
    }

    public ProjectJson update(ProjectJson project) {
        Project proj = projectRepository.findByProjectId(project.getProjectId());
        if (proj != null && !project.getId().isEmpty()) {
            proj.setProjectId(project.getProjectId());
            proj.setProjectName(project.getName());
            projectRepository.save(proj);
            return project;
        }
        return null;
    }

    public ProjectsResponse read(String projectId) {
        return null;
    }

    public boolean exists(String projectId) {
        Project project = this.projectRepository.findByProjectId(projectId);
        return project != null && project.getProjectId().equals(projectId);
    }
}
