package org.openmbee.mms.crud.controllers.projects;

import io.swagger.v3.oas.annotations.tags.Tag;

import org.openmbee.mms.core.config.ContextHolder;
import org.openmbee.mms.core.config.Privileges;
import org.openmbee.mms.core.config.ProjectSchemas;
import org.openmbee.mms.core.dao.ProjectDAO;
import org.openmbee.mms.core.dao.ProjectIndex;
import org.openmbee.mms.core.exceptions.MMSException;
import org.openmbee.mms.core.objects.ProjectsRequest;
import org.openmbee.mms.core.objects.ProjectsResponse;
import org.openmbee.mms.core.exceptions.DeletedException;
import org.openmbee.mms.core.objects.Rejection;
import org.openmbee.mms.data.domains.global.Project;
import org.openmbee.mms.crud.controllers.BaseController;
import org.openmbee.mms.core.exceptions.BadRequestException;
import org.openmbee.mms.core.exceptions.NotFoundException;
import org.openmbee.mms.core.services.ProjectService;
import org.openmbee.mms.json.ProjectJson;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/projects")
@Tag(name = "Projects")
public class ProjectsController extends BaseController {

    private static final String PROJECT_ID_VALID_PATTERN = "^[\\w-]+$";

    ProjectDAO projectRepository;
    ProjectIndex projectIndex;
    ProjectSchemas schemas;


    @Autowired
    public ProjectsController(ProjectDAO projectRepository, ProjectIndex projectIndex, ProjectSchemas schemas) {
        this.projectRepository = projectRepository;
        this.projectIndex = projectIndex;
        this.schemas = schemas;
    }

    @GetMapping
    public ProjectsResponse getAllProjects(Authentication auth,
                                           @RequestParam(required = false) String orgId) {
        ProjectsResponse response = new ProjectsResponse();
        List<Project> allProjects = orgId != null ? projectRepository.findAllByOrgId(orgId) : projectRepository.findAll();
        for (Project proj : allProjects) {
            if (mss.hasProjectPrivilege(auth, proj.getProjectId(), Privileges.PROJECT_READ.name(), true)) {
                ContextHolder.setContext(proj.getProjectId());
                if(proj.getDocId() != null  && !proj.isDeleted()) {
                    Optional<ProjectJson> projectJsonOption = projectIndex.findById(proj.getDocId());
                    projectJsonOption.ifPresentOrElse(json -> response.getProjects().add(json), ()-> {
                        logger.error("Project json not found for id: {}", proj.getProjectId());
                    });
                }
            }
        }
        return response;
    }

    @GetMapping(value = "/{projectId}")
    @PreAuthorize("@mss.hasProjectPrivilege(authentication, #projectId, 'PROJECT_READ', true)")
    public ProjectsResponse getProject(
        @PathVariable String projectId) {

        ContextHolder.setContext(projectId);
        ProjectsResponse response = new ProjectsResponse();
        Optional<Project> projectOption = projectRepository.findByProjectId(projectId);
        if (!projectOption.isPresent()) {
            throw new NotFoundException(response.addMessage("Project not found"));
        }
        Optional<ProjectJson> projectJsonOption = projectIndex.findById(projectOption.get().getDocId());
        projectJsonOption.ifPresentOrElse(json -> response.getProjects().add(json), ()-> {
            throw new NotFoundException(response.addMessage("Project JSON not found"));
        });
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
            try {
                if (json.getProjectId() == null || json.getProjectId().isEmpty()) {
                    json.setId(UUID.randomUUID().toString());
                }
                if (!isProjectIdValid(json.getProjectId())) {
                    response.addRejection(new Rejection(json, 400, "Project id is invalid."));
                    continue;
                }
                if ((json.getProjectType() != null) && (!((schemas.getSchemas())
                    .containsKey(json.getProjectType())))) {
                    response.addRejection(new Rejection(json, 400, "Project schema is unknown."));
                    continue;
                }
                ProjectService ps = getProjectService(json);
                if (!ps.exists(json.getProjectId())) {
                    if (!mss.hasOrgPrivilege(auth, json.getOrgId(),
                        Privileges.ORG_CREATE_PROJECT.name(), false)) {
                        response.addRejection(new Rejection(json, 403, "No permission to create project under org"));
                        continue;
                    }
                    if (json.getCreator() == null || json.getCreator().isEmpty()) {
                        json.setCreator(auth.getName());
                    }
                    response.getProjects().add(ps.create(json));
                    permissionService.initProjectPerms(json.getProjectId(), true, auth.getName());
                } else {
                    if (!mss.hasProjectPrivilege(auth, json.getProjectId(),
                        Privileges.PROJECT_EDIT.name(), false)) {
                        response.addRejection(new Rejection(json, 403, "No permission to change project"));
                        continue;
                    }
                    boolean updateInheritedPerms = false;
                    if (json.getOrgId() != null && !json.getOrgId().isEmpty()) {
                        Project proj = projectRepository.findByProjectId(json.getProjectId()).get();
                        String existingOrg = proj.getOrgId();
                        if (!json.getOrgId().equals(existingOrg)) {
                            if (!mss.hasProjectPrivilege(auth, json.getProjectId(), Privileges.PROJECT_DELETE.name(), false) ||
                                !mss.hasOrgPrivilege(auth, json.getOrgId(), Privileges.ORG_CREATE_PROJECT.name(), false)) {
                                response.addRejection(
                                    new Rejection(json, 403, "No permission to move project org"));
                                continue;
                            }
                            if (proj.isInherit()) {
                                updateInheritedPerms = true;
                            }
                        }
                    }
                    response.getProjects().add(ps.update(json));
                    if (updateInheritedPerms) {
                        permissionService.setProjectInherit(false, json.getProjectId());
                        permissionService.setProjectInherit(true, json.getProjectId());
                    }
                }
            } catch (MMSException ex) {
                response.addRejection(new Rejection(json, ex.getCode().value(), ex.getMessageObject().toString()));
                continue;
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
            projectIndex.delete(projectId);
        } else {
            projectRepository.save(project);
            // TODO soft delete for index?
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
