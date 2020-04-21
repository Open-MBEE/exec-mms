package org.openmbee.sdvc.crud.services;

import java.util.Optional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openmbee.sdvc.core.dao.OrgDAO;
import org.openmbee.sdvc.core.dao.ProjectDAO;
import org.openmbee.sdvc.core.objects.EventObject;
import org.openmbee.sdvc.core.services.EventService;
import org.openmbee.sdvc.core.services.ProjectService;
import org.openmbee.sdvc.core.exceptions.InternalErrorException;
import org.openmbee.sdvc.data.domains.global.Organization;
import org.openmbee.sdvc.data.domains.global.Project;
import org.openmbee.sdvc.core.exceptions.BadRequestException;
import org.openmbee.sdvc.core.dao.ProjectIndex;
import org.openmbee.sdvc.json.ProjectJson;
import org.openmbee.sdvc.core.objects.ProjectsResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("defaultProjectService")
public class DefaultProjectService implements ProjectService {

    protected final Logger logger = LogManager.getLogger(getClass());
    protected ProjectDAO projectRepository;
    protected OrgDAO orgRepository;
    protected ProjectIndex projectIndex;
    protected Optional<EventService> eventPublisher;

    @Autowired
    public void setProjectRepository(ProjectDAO projectRepository) {
        this.projectRepository = projectRepository;
    }

    @Autowired
    public void setOrganizationRepository(OrgDAO orgRepository) {
        this.orgRepository = orgRepository;
    }

    @Autowired
    public void setProjectIndex(ProjectIndex projectIndex) {
        this.projectIndex = projectIndex;
    }

    @Autowired
    public void setEventPublisher(Optional<EventService> eventPublisher) {
        this.eventPublisher = eventPublisher;
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
        try {
            projectRepository.save(proj);
            projectIndex.create(proj.getProjectId(), project.getProjectType());
            projectIndex.update(project);
            eventPublisher.ifPresent((pub) -> pub.publish(
                EventObject.create(project.getId(), "master", "project_created", project)));
            return project;
        } catch (Exception e) {
            logger.error("Couldn't create project: {}", project.getProjectId());
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
            projectIndex.update(project);

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
