package org.openmbee.sdvc.crud.services;

import java.sql.SQLException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openmbee.sdvc.core.domains.Project;
import org.openmbee.sdvc.core.repositories.ProjectRepository;
import org.openmbee.sdvc.crud.controllers.projects.ProjectJson;
import org.openmbee.sdvc.crud.controllers.projects.ProjectsRequest;
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

    public ProjectsResponse post(ProjectsRequest projectsPost) {
        logger.info("JSON parsed properly");
        ProjectsResponse response = new ProjectsResponse();
        for (ProjectJson project : projectsPost.getProjects()) {
            logger.info("Saving project: {}", project.getId());
            Project proj = new Project();
            proj.setProjectId(project.getId());
            proj.setProjectName(project.getName());
            Project saved = projectRepository.save(proj);

            try {
                if (projectOperations.createProjectDatabase(proj)) {
                    response.getProjects().add(project);
                }
            } catch (SQLException sqlException) {
                // Database already exists
                response.getProjects().add(project);
            } catch (Exception e) {
                projectRepository.delete(saved);
                logger.error("Couldn't create database: {}", project.getProjectId());
                logger.error(e);
            }
        }
        return response;
    }

    public ProjectsResponse get(String projectId) {
        return null;
    }
}
