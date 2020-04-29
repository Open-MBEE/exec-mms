package org.openmbee.sdvc.crud.controllers.projects;

import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.openmbee.sdvc.core.config.Privileges;
import org.openmbee.sdvc.core.dao.ProjectDAO;
import org.openmbee.sdvc.core.objects.ProjectsRequest;
import org.openmbee.sdvc.core.objects.ProjectsResponse;
import org.openmbee.sdvc.core.exceptions.DeletedException;
import org.openmbee.sdvc.core.objects.Rejection;
import org.openmbee.sdvc.data.domains.global.Project;
import org.openmbee.sdvc.crud.controllers.BaseController;
import org.openmbee.sdvc.core.exceptions.BadRequestException;
import org.openmbee.sdvc.core.exceptions.NotFoundException;
import org.openmbee.sdvc.core.services.ProjectService;
import org.openmbee.sdvc.json.ProjectJson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
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
@Tag(name = "Projects")
public class ProjectsController extends BaseController {

    private static final String PROJECT_ID_VALID_PATTERN = "^[\\w-]+$";

    ProjectDAO projectRepository;

    @Autowired
    public ProjectsController(ProjectDAO projectRepository) {
        this.projectRepository = projectRepository;
    }

    @GetMapping
    public ProjectsResponse getAllProjects(Authentication auth) {

        ProjectsResponse response = new ProjectsResponse();
        List<Project> allProjects = projectRepository.findAll();
        for (Project proj : allProjects) {
            if (mss.hasProjectPrivilege(auth, proj.getProjectId(), Privileges.PROJECT_READ.name(), true)) {
                ProjectJson projectJson = new ProjectJson();
                projectJson.merge(convertToMap(proj));
                response.getProjects().add(projectJson);
            }
        }
        return response;
    }

    @GetMapping(value = "/{projectId}")
    @PreAuthorize("@mss.hasProjectPrivilege(authentication, #projectId, 'PROJECT_READ', true)")
    public ProjectsResponse getProject(
        @PathVariable String projectId) {

        ProjectsResponse response = new ProjectsResponse();
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
        return response;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    @PreAuthorize("isAuthenticated()")
    public ProjectsResponse createOrUpdateProjects(
        @RequestBody ProjectsRequest projectsPost,
        Authentication auth) {

        if (projectsPost.getProjects().isEmpty()) {
            throw new BadRequestException(new ProjectsResponse().addMessage("No projects provided"));
        }

        ProjectsResponse response = new ProjectsResponse();
        for (ProjectJson json: projectsPost.getProjects()) {
            if (json.getProjectId().isEmpty()) {
                response.addRejection(new Rejection(json, 400, "Project id missing"));
                continue;
            }
            if(! isProjectIdValid(json.getProjectId())) {
                response.addRejection(new Rejection(json, 400, "Project id is invalid."));
                continue;
            }

            ProjectService ps = getProjectService(json);
            if (!ps.exists(json.getProjectId())) {
                try {
                    if (!mss.hasOrgPrivilege(auth, json.getOrgId(), Privileges.ORG_CREATE_PROJECT.name(), false)) {
                        response.addRejection(new Rejection(json, 403, "No permission to create project under org"));
                        continue;
                    }

                    if(json.getCreator() == null || json.getCreator().isEmpty()) {
                        json.setCreator(auth.getName());
                    }
                    if(json.getModifier() == null || json.getModifier().isEmpty()) {
                        json.setModifier(auth.getName());
                    }

                    response.getProjects().add(ps.create(json));
                    permissionService.initProjectPerms(json.getProjectId(), true, auth.getName());
                } catch (BadRequestException ex) {
                    response.addRejection(new Rejection(json, 400, "Org to put project under is not found"));
                    continue;
                }
            } else {
                if (!mss.hasProjectPrivilege(auth, json.getProjectId(), Privileges.PROJECT_EDIT.name(), false)) {
                    response.addRejection(new Rejection(json, 403, "No permission to change project"));
                    continue;
                }
                //TODO need to check delete perm on proj and create perm in new org if moving org, and reset project perms if org changed
                response.getProjects().add(ps.update(json));
            }
        }
        if (projectsPost.getProjects().size() == 1) {
            handleSingleResponse(response);
        }
        return response;
    }


    @DeleteMapping(value = "/{projectId}")
    @Transactional
    @PreAuthorize("@mss.hasProjectPrivilege(authentication, #projectId, 'PROJECT_DELETE', false)")
    public ProjectsResponse deleteProject(
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
        return response.setProjects(res);
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

    static boolean isProjectIdValid(String projectId) {
        return projectId != null && projectId.matches(PROJECT_ID_VALID_PATTERN);
    }
}
