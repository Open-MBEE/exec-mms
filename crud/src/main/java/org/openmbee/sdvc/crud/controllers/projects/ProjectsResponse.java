package org.openmbee.sdvc.crud.controllers.projects;

import java.util.ArrayList;
import java.util.List;
import org.openmbee.sdvc.core.domains.Project;
import org.openmbee.sdvc.crud.controllers.BaseResponse;

public class ProjectsResponse extends BaseResponse {

    private List<Project> projects = new ArrayList<>();

    public ProjectsResponse() {
    }

    public List<Project> getProjects() {
        return projects;
    }

    public void setProjects(List<Project> projects) {
        this.projects = projects;
    }

}
