package org.openmbee.sdvc.crud.controllers.projects;

import com.fasterxml.jackson.core.type.TypeReference;
import java.util.List;
import java.util.Map;
import org.openmbee.sdvc.core.domains.Project;
import org.openmbee.sdvc.core.repositories.ProjectRepository;
import org.openmbee.sdvc.crud.controllers.BaseController;
import org.openmbee.sdvc.crud.controllers.BaseResponse;
import org.openmbee.sdvc.crud.services.ProjectService;
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
    public ResponseEntity<?> handleGet(@PathVariable(required = false) String projectId) {
        ProjectsResponse response = new ProjectsResponse();

        if (projectId != null) {
            logger.debug("ProjectId given: ", projectId);
            Project org = projectRepository.findByProjectId(projectId);
            ProjectJson projectJson = new ProjectJson();
            Map<String, Object> converted = om.convertValue(org, new TypeReference<Map<String, Object>>() {});
            projectJson.merge(converted);
            response.getProjects().add(projectJson);
            return ResponseEntity.ok(response);
        } else {
            logger.debug("No ProjectId given");
            List<Project> allOrgs = projectRepository.findAll();
            for (Project org : allOrgs) {
                ProjectJson projectJson = new ProjectJson();
                Map<String, Object> converted = om.convertValue(org, new TypeReference<Map<String, Object>>() {});
                projectJson.merge(converted);
                response.getProjects().add(projectJson);
            }
            return ResponseEntity.ok(response);
        }
    }

    @PostMapping
    public ResponseEntity<? extends BaseResponse> handlePost(
        @RequestBody ProjectsRequest projectsPost) {

        if (projectsPost.getProjects().isEmpty()) {
            ProjectsResponse pr = new ProjectsResponse();
            pr.addMessage("empty");
            return ResponseEntity.badRequest().body(pr);
        }
        ProjectsResponse response = new ProjectsResponse();
        for (ProjectJson json: projectsPost.getProjects()) {
            ProjectService ps = getProjectService(json.getProjectId());
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
        return ResponseEntity.ok(new ProjectsResponse());
    }

    private ProjectService getProjectService(String projectId) {
        return serviceFactory.getProjectService("cameo");
    }
}
