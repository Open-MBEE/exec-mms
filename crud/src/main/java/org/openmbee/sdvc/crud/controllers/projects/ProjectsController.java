package org.openmbee.sdvc.crud.controllers.projects;

import java.util.List;
import java.util.Optional;
import javax.transaction.Transactional;

import org.openmbee.sdvc.core.objects.ProjectsRequest;
import org.openmbee.sdvc.core.objects.ProjectsResponse;
import org.openmbee.sdvc.data.domains.global.Project;
import org.openmbee.sdvc.rdb.repositories.ProjectRepository;
import org.openmbee.sdvc.crud.controllers.BaseController;
import org.openmbee.sdvc.core.objects.BaseResponse;
import org.openmbee.sdvc.crud.exceptions.BadRequestException;
import org.openmbee.sdvc.crud.exceptions.NotFoundException;
import org.openmbee.sdvc.core.services.ProjectService;
import org.openmbee.sdvc.json.ProjectJson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/projects")
public class ProjectsController extends BaseController {

    ProjectRepository projectRepository;

    @Autowired
    public ProjectsController(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    @GetMapping(value = {"", "/{projectId}"})
    @Transactional
    public ResponseEntity<? extends BaseResponse> handleGet(
        @PathVariable(required = false) String projectId) {
        ProjectsResponse response = new ProjectsResponse();

        if (projectId != null) {
            logger.debug("ProjectId given: ", projectId);
            Optional<Project> projectOption = projectRepository.findByProjectId(projectId);
            if (!projectOption.isPresent()) {
                response.addMessage("Project not found");
                throw new NotFoundException(response);
            }
            ProjectJson projectJson = new ProjectJson();
            projectJson.merge(convertToMap(projectOption.get()));
            response.getProjects().add(projectJson);
        } else {
            logger.debug("No ProjectId given");
            List<Project> allOrgs = projectRepository.findAll();
            for (Project org : allOrgs) {
                ProjectJson projectJson = new ProjectJson();
                projectJson.merge(convertToMap(org));
                response.getProjects().add(projectJson);
            }
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping
    @Transactional
    public ResponseEntity<? extends BaseResponse> handlePost(
        @RequestBody ProjectsRequest projectsPost) {

        if (projectsPost.getProjects().isEmpty()) {
            throw new BadRequestException(new ProjectsResponse().addMessage("No projects"));
        }

        ProjectsResponse response = new ProjectsResponse();
        for (ProjectJson json: projectsPost.getProjects()) {
            if (json.getProjectId().isEmpty()) {
                response.addMessage("Project ID is missing");
                continue;
            }
            ProjectService ps = getProjectService(json);
            if (!ps.exists(json.getProjectId())) {
                response.getProjects().add(ps.create(json));
            } else {
                response.getProjects().add(ps.update(json));
            }
        }
        return ResponseEntity.ok(response);
    }

    @DeleteMapping(value = "/{projectId}")
    public ResponseEntity<? extends BaseResponse> handleDelete(@PathVariable String projectId) {
        return ResponseEntity.ok(new ProjectsResponse()); //TODO
    }

    private ProjectService getProjectService(ProjectJson json) {
        String type = json.getProjectType();
        if (type == null || type.isEmpty()) {
            try {
                type = this.getProjectType(json.getProjectId());
            } catch (NotFoundException e) {
                type = "default";
            }
            json.setProjectType(type);
        }
        return serviceFactory.getProjectService(type);
    }
}
