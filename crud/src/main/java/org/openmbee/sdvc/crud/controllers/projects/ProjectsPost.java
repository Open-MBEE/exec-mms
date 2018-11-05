package org.openmbee.sdvc.crud.controllers.projects;

import java.sql.SQLException;
import org.openmbee.sdvc.core.domains.Project;
import org.openmbee.sdvc.core.repositories.ProjectRepository;
import org.openmbee.sdvc.crud.controllers.BaseController;
import org.openmbee.sdvc.crud.controllers.BaseResponse;
import org.openmbee.sdvc.crud.controllers.ErrorResponse;
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
    private ProjectsOperations projectsOperations;

    @Autowired
    public ProjectsPost(ProjectRepository projectRepository, ProjectsOperations projectsOperations) {
        this.projectRepository = projectRepository;
        this.projectsOperations = projectsOperations;
    }

    @PostMapping
    public ResponseEntity<? extends BaseResponse> handleRequest(
        @RequestBody ProjectsRequest projectsPost) {
        if (!projectsPost.getProjects().isEmpty()) {
            logger.info("JSON parsed properly");
            ProjectsResponse response = new ProjectsResponse();

            for (Project project : projectsPost.getProjects()) {
                logger.info("Saving project: {}", project.getProjectId());
                Project saved = projectRepository.save(project);

                try {
                    if (projectsOperations.createProjectDatabase(project)) {
                        response.getProjects().add(saved);
                    }
                } catch (SQLException sqlException) {
                    // Database already exists
                    response.getProjects().add(saved);
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
