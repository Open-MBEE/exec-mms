package org.openmbee.sdvc.crud.controllers.projects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.transaction.Transactional;

import org.openmbee.sdvc.core.config.Privileges;
import org.openmbee.sdvc.core.dao.ProjectDAO;
import org.openmbee.sdvc.core.objects.ProjectsRequest;
import org.openmbee.sdvc.core.objects.ProjectsResponse;
import org.openmbee.sdvc.core.exceptions.DeletedException;
import org.openmbee.sdvc.data.domains.global.Project;
import org.openmbee.sdvc.crud.controllers.BaseController;
import org.openmbee.sdvc.core.objects.BaseResponse;
import org.openmbee.sdvc.core.exceptions.BadRequestException;
import org.openmbee.sdvc.core.exceptions.NotFoundException;
import org.openmbee.sdvc.core.services.ProjectService;
import org.openmbee.sdvc.json.ProjectJson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/projects")
public class ProjectsController extends BaseController {

    ProjectDAO projectRepository;

    @Autowired
    public ProjectsController(ProjectDAO projectRepository) {
        this.projectRepository = projectRepository;
    }

    @GetMapping(value = {"", "/{projectId}"})
    @PreAuthorize("#projectId == null || @mss.hasProjectPrivilege(authentication, #projectId, 'PROJECT_READ', true)")
    public ResponseEntity<? extends BaseResponse> handleGet(
        @PathVariable(required = false) String projectId,
        Authentication auth) {

        ProjectsResponse response = new ProjectsResponse();
        if (projectId != null) {
            Optional<Project> projectOption = projectRepository.findByProjectId(projectId);
            if (!projectOption.isPresent()) {
                throw new NotFoundException(response.addMessage("Project not found"));
            }
            ProjectJson projectJson = new ProjectJson();
            projectJson.merge(convertToMap(projectOption.get()));
            response.getProjects().add(projectJson);
            if (projectOption.get().isDeleted()) {
                throw new DeletedException(response);
            }
        } else {
            List<Project> allProjects = projectRepository.findAll();
            for (Project proj : allProjects) {
                if (mss.hasProjectPrivilege(auth, proj.getProjectId(), Privileges.PROJECT_READ.name(), true)) {
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
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<? extends BaseResponse> handlePost(
        @RequestBody ProjectsRequest projectsPost,
        Authentication auth) {

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
                    if (!mss.hasOrgPrivilege(auth, json.getOrgId(), Privileges.ORG_CREATE_PROJECT.name(), false)) {
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
                    rejection.put("message", "Org to put project under is not found");
                    rejection.put("code", 400);
                    rejection.put("project", json);
                    rejected.add(rejection);
                    continue;
                }
            } else {
                if (!mss.hasProjectPrivilege(auth, json.getProjectId(), Privileges.PROJECT_EDIT.name(), false)) {
                    Map<String, Object> rejection = new HashMap<>();
                    rejection.put("message", "No permission to change project");
                    rejection.put("code", 403);
                    rejection.put("project", json);
                    rejected.add(rejection);
                    continue;
                }
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
    @Transactional
    @PreAuthorize("@mss.hasProjectPrivilege(authentication, #projectId, 'PROJECT_DELETE', false)")
    public ResponseEntity<? extends BaseResponse> handleDelete(
        @PathVariable String projectId,
        @RequestParam(required = false, defaultValue = "false") boolean hard) {

        ProjectsResponse response = new ProjectsResponse();
        Optional<Project> projectOption = projectRepository.findByProjectId(projectId);
        if (!projectOption.isPresent()) {
            throw new NotFoundException(response.addMessage("Project not found"));
        }
        Project project = projectOption.get();
        project.setDeleted(true);
        ProjectJson projectJson = new ProjectJson();
        projectJson.merge(convertToMap(project));
        List<ProjectJson> res = new ArrayList<>();
        res.add(projectJson);
        if (hard) {
            projectRepository.delete(project);
        } else {
            projectRepository.save(project);
        }
        return ResponseEntity.ok(response.setProjects(res));
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
