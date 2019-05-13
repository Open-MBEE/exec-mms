package org.openmbee.sdvc.crud.services;

import java.sql.SQLException;
import java.util.Optional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openmbee.sdvc.crud.exceptions.InternalErrorException;
import org.openmbee.sdvc.data.domains.Organization;
import org.openmbee.sdvc.data.domains.Project;
import org.openmbee.sdvc.core.repositories.OrganizationRepository;
import org.openmbee.sdvc.core.repositories.ProjectRepository;
import org.openmbee.sdvc.crud.exceptions.BadRequestException;
import org.openmbee.sdvc.crud.exceptions.NotFoundException;
import org.openmbee.sdvc.crud.repositories.ProjectIndex;
import org.openmbee.sdvc.json.ProjectJson;
import org.openmbee.sdvc.crud.controllers.projects.ProjectsResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("defaultProjectService")
public class DefaultProjectService implements ProjectService {

    protected final Logger logger = LogManager.getLogger(getClass());
    protected ProjectRepository projectRepository;
    protected OrganizationRepository orgRepository;
    protected DatabaseDefinitionService projectOperations;
    protected ProjectIndex projectIndex;

    @Autowired
    public void setProjectRepository(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    @Autowired
    public void setOrganizationRepository(OrganizationRepository orgRepository) {
        this.orgRepository = orgRepository;
    }

    @Autowired
    public void setDatabaseDefinitionService(DatabaseDefinitionService projectOperations) {
        this.projectOperations = projectOperations;
    }

    @Autowired
    public void setProjectIndex(ProjectIndex projectIndex) {
        this.projectIndex = projectIndex;
    }

    public ProjectJson create(ProjectJson project) {
        if (project.getOrgId() == null || project.getOrgId().isEmpty()) {
            throw new BadRequestException(new ProjectsResponse().addMessage("Organization ID not provided"));
        }

        Optional<Organization> org = orgRepository.findByOrganizationId(project.getOrgId());
        if (!org.isPresent() || org.get().getOrganizationId().isEmpty()) {
            throw new BadRequestException(new ProjectsResponse().addMessage("Organization not found"));
        }

        Project proj = new Project();
        proj.setProjectId(project.getId());
        proj.setProjectName(project.getName());
        proj.setOrganization(org.get());
        proj.setProjectType(project.getProjectType());
        Project saved = projectRepository.save(proj);

        try {
            if (projectOperations.createProjectDatabase(proj)) {
                projectIndex.create(proj.getProjectId(), project.getProjectType());
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
        throw new InternalErrorException("Could not create project");
    }

    public ProjectJson update(ProjectJson project) {
        Optional<Project> projOption = projectRepository.findByProjectId(project.getProjectId());
        if (projOption.isPresent() && !projOption.get().getProjectId().isEmpty()) {
            Project proj = projOption.get();
            proj.setProjectId(project.getProjectId());
            proj.setProjectName(project.getName());
            if (!project.getOrgId().isEmpty()) {
                Optional<Organization> org = orgRepository.findByOrganizationId(project.getOrgId());
                if (org.isPresent() && !org.get().getOrganizationId().isEmpty()) {
                    proj.setOrganization(org.get());
                } else {
                    throw new BadRequestException("Invalid organization");
                }
            }
            projectRepository.save(proj);
            return project;
        }
        throw new InternalErrorException("Could not update project");
    }

    public ProjectsResponse read(String projectId) {
        return null;
    }

    public boolean exists(String projectId) {
        Optional<Project> project = this.projectRepository.findByProjectId(projectId);
        return project.isPresent() && project.get().getProjectId().equals(projectId);
    }
}
