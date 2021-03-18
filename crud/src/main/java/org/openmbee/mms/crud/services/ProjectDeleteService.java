package org.openmbee.mms.crud.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.openmbee.mms.core.config.ContextHolder;
import org.openmbee.mms.core.dao.ProjectDAO;
import org.openmbee.mms.core.dao.ProjectIndex;
import org.openmbee.mms.core.exceptions.NotFoundException;
import org.openmbee.mms.core.objects.ProjectsResponse;
import org.openmbee.mms.data.domains.global.Project;
import org.openmbee.mms.json.ProjectJson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ProjectDeleteService {

    private ProjectDAO projectRepository;
    private ProjectIndex projectIndex;
    protected ObjectMapper om;

    @Autowired
    public void setProjectRepository(ProjectDAO projectRepository) {
        this.projectRepository = projectRepository;
    }

    @Autowired
    public void setProjectIndex(ProjectIndex projectIndex) {
        this.projectIndex = projectIndex;
    }

    @Autowired
    public void setOm(ObjectMapper om) {
        this.om = om;
    }

    public ProjectsResponse deleteProject(String projectId, boolean hard) {
        ProjectsResponse response = new ProjectsResponse();
        ContextHolder.setContext(projectId);
        Optional<Project> projectOption = projectRepository.findByProjectId(projectId);
        Optional<ProjectJson> projectJsonOption = projectOption.isPresent() ?
            projectIndex.findById(projectOption.get().getDocId()) : Optional.empty();

        List<ProjectJson> res = new ArrayList<>();

        //Do not try to do a soft delete when an error condition is present.
        if(!hard && (! projectOption.isPresent() || !projectJsonOption.isPresent())) {
            throw new NotFoundException("Project state is invalid, cannot soft delete.");
        }

        if(hard) {
            projectRepository.delete(projectId);
            projectIndex.delete(projectId);
        } else {
            Project project = projectOption.get();
            project.setDeleted(true);
            projectRepository.save(project);
            // TODO soft delete for index?
        }

        ProjectJson projectJson;
        if(projectJsonOption.isPresent()) {
            projectJson = projectJsonOption.get();
        } else if(projectOption.isPresent()){
            projectJson = new ProjectJson();
            projectJson.merge(convertToMap(projectOption.get()));
        } else {
            projectJson = new ProjectJson();
            projectJson.setProjectId(projectId);
        }
        projectJson.put("deleted", "true");
        res.add(projectJson);

        return response.setProjects(res);
    }

    private Map<String, Object> convertToMap(Object obj) {
        return om.convertValue(obj, new TypeReference<Map<String, Object>>() {});
    }
}
