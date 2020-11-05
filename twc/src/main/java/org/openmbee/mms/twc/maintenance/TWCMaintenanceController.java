package org.openmbee.mms.twc.maintenance;

import org.openmbee.mms.core.config.AuthorizationConstants;
import org.openmbee.mms.core.exceptions.InternalErrorException;
import org.openmbee.mms.core.exceptions.NotFoundException;
import org.openmbee.mms.data.domains.global.Project;
import org.openmbee.mms.rdb.repositories.ProjectRepository;
import org.openmbee.mms.twc.metadata.TwcMetadata;
import org.openmbee.mms.twc.metadata.TwcMetadataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;


@RestController
@RequestMapping("/adm/maintenance")
public class TWCMaintenanceController {

    private ProjectRepository projectRepo;
    private TwcMetadataService twcMetadataService;

    @Autowired
    public void setProjectRepo(ProjectRepository projectRepo){
        this.projectRepo = projectRepo;
    }

    @Autowired
    public void setTwcMetadataService(TwcMetadataService twcMetadataService){
        this.twcMetadataService = twcMetadataService;
    }

    @Transactional
    @PreAuthorize(AuthorizationConstants.IS_MMSADMIN)
    @GetMapping(value = "/project/twcmetadata/{id}")
    @ResponseBody
    public TwcMetadata  getProjectMetadata(@PathVariable String id){
        Project project = getProject(id);
        return twcMetadataService.getTwcMetadata(project);
    }


    @Transactional
    @PreAuthorize(AuthorizationConstants.IS_MMSADMIN)
    @PostMapping(value = "/project/twcmetadata/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public void updateProjectMetadata(@PathVariable String id, @RequestBody TwcMetadata twcMetadata){

        Project project = getProject(id);
        try{
            twcMetadataService.updateTwcMetadata(project, twcMetadata);
        }
        catch (Exception ex){
            throw new InternalErrorException(ex.getMessage());
        }
    }

    @Transactional
    @PreAuthorize(AuthorizationConstants.IS_MMSADMIN)
    @DeleteMapping(value = "/project/twcmetadata/{id}")
    @ResponseBody
    public void deleteProjectMetadata(@PathVariable String id){
        Project project = getProject(id);
        twcMetadataService.deleteTwcMetadata(project);
    }

    private Project getProject(String projectId) {

        Optional<Project> proj = projectRepo.findByProjectId(projectId);

        return proj.orElseGet(() -> {
            String notFound = "Project id: " + projectId + " not found";
            throw new NotFoundException(notFound);
        });

    }
}

