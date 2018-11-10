package org.openmbee.sdvc.crud.controllers.projects;

import java.sql.SQLException;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import org.openmbee.sdvc.core.domains.Project;
import org.openmbee.sdvc.core.repositories.ProjectRepository;
import org.openmbee.sdvc.crud.controllers.BaseController;
import org.openmbee.sdvc.crud.controllers.BaseResponse;
import org.openmbee.sdvc.crud.controllers.Constants;
import org.openmbee.sdvc.crud.controllers.ErrorResponse;
import org.openmbee.sdvc.crud.services.DatabaseDefinitionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/projects")
public class ProjectsPost extends BaseController {

    private ProjectRepository projectRepository;
    private DatabaseDefinitionService projectsOperations;

    @Autowired
    public ProjectsPost(ProjectRepository projectRepository, DatabaseDefinitionService projectsOperations) {
        this.projectRepository = projectRepository;
        this.projectsOperations = projectsOperations;
    }

    @PostMapping
    public ResponseEntity<? extends BaseResponse> handleRequest(
        @RequestBody ProjectsRequest projectsPost) {
        if (!projectsPost.getProjects().isEmpty()) {
            logger.info("JSON parsed properly");
            ProjectsResponse response = new ProjectsResponse();
            for (ProjectJson project : projectsPost.getProjects()) {
                logger.info("Saving project: {}", project.getId());
                Project proj = new Project();
                proj.setProjectId(project.getId());
                proj.setProjectName(project.getName());
                Project saved = projectRepository.save(proj);

                try {
                    if (projectsOperations.createProjectDatabase(proj)) {
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

            return ResponseEntity.ok(response);
        }
        logger.debug("Bad Request");
        ErrorResponse err = new ErrorResponse();
        err.setCode(400);
        err.setError("Bad Request");
        return ResponseEntity.badRequest().body(err);
    }
}
