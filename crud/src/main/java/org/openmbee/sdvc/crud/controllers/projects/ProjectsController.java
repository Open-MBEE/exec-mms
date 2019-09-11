package org.openmbee.sdvc.crud.controllers.projects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.transaction.Transactional;

import org.openmbee.sdvc.core.objects.ProjectsRequest;
import org.openmbee.sdvc.core.objects.ProjectsResponse;
import org.openmbee.sdvc.crud.exceptions.ForbiddenException;
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
import org.springframework.security.core.Authentication;
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
        @PathVariable(required = false) String projectId,
        Authentication auth) {

        ProjectsResponse response = new ProjectsResponse();
        if (projectId != null) {
            Optional<Project> projectOption = projectRepository.findByProjectId(projectId);
            if (!projectOption.isPresent()) {
                throw new NotFoundException(response.addMessage("Project not found"));
            }
            if (!permissionService.isProjectPublic(projectId)) {
                rejectAnonymous(auth);
                if (!permissionService.hasProjectPrivilege("PROJECT_READ", auth.getName(), projectId)) {
                    throw new ForbiddenException(response.addMessage("No permission for project"));
                }
            }
            ProjectJson projectJson = new ProjectJson();
            projectJson.merge(convertToMap(projectOption.get()));
            response.getProjects().add(projectJson);
        } else {
            List<Project> allProjects = projectRepository.findAll();
            for (Project proj : allProjects) {
                if ((isAnonymous(auth) && permissionService.isProjectPublic(proj.getProjectId())) ||
                        permissionService.hasProjectPrivilege("PROJECT_READ", auth.getName(), proj.getProjectId())) {
                    ProjectJson projectJson = new ProjectJson();
                    projectJson.merge(convertToMap(proj));
                    response.getProjects().add(projectJson);
                }
            }
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping
    @Transactional
    public ResponseEntity<? extends BaseResponse> handlePost(
        @RequestBody ProjectsRequest projectsPost,
        Authentication auth) {

        rejectAnonymous(auth);
        if (projectsPost.getProjects().isEmpty()) {
            throw new BadRequestException(new ProjectsResponse().addMessage("No projects provided"));
        }

        ProjectsResponse response = new ProjectsResponse();
        List<Map> rejected = new ArrayList<>();
        response.setRejected(rejected);
        for (ProjectJson json: projectsPost.getProjects()) {
            if (json.getProjectId().isEmpty()) {
                Map<String, Object> rejection = new HashMap<>();
                rejection.put("message", "Project id missing");
                rejection.put("code", 400);
                rejection.put("project", json);
                rejected.add(rejection);
                continue;
            }
            ProjectService ps = getProjectService(json);
            if (!ps.exists(json.getProjectId())) {
                try {
                    if (!permissionService.hasOrgPrivilege("ORG_CREATE_PROJECT", auth.getName(), json.getOrgId())) {
                        Map<String, Object> rejection = new HashMap<>();
                        rejection.put("message", "No permission to create project under org");
                        rejection.put("code", 403);
                        rejection.put("project", json);
                        rejected.add(rejection);
                        continue;
                    }
                    response.getProjects().add(ps.create(json));
                    permissionService.initProjectPerms(json.getProjectId(), true, auth.getName());
                } catch (BadRequestException ex) {
                    Map<String, Object> rejection = new HashMap<>();
                    rejection.put("message", "Org not found");
                    rejection.put("code", 400);
                    rejection.put("project", json);
                    rejected.add(rejection);
                    continue;
                }
            } else {
                //TODO need to check delete perm on proj and create perm in new org if moving org, and reset project perms if org changed
                response.getProjects().add(ps.update(json));
            }
        }
        if (projectsPost.getProjects().size() == 1) {
            handleSingleResponse(response);
        }
        return ResponseEntity.ok(response);
    }

    @DeleteMapping(value = "/{projectId}")
    public ResponseEntity<? extends BaseResponse> handleDelete(
        @PathVariable String projectId,
        Authentication auth) {

        rejectAnonymous(auth);
        if (!permissionService.hasProjectPrivilege("PROJECT_DELETE", auth.getName(), projectId)) {
            throw new ForbiddenException(new ProjectsResponse().addMessage("No permission to delete project."));
        }
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
