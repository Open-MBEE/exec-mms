package org.openmbee.sdvc.crud.controllers.projects;

import java.util.ArrayList;
import java.util.List;
import org.openmbee.sdvc.core.domains.Project;
import org.openmbee.sdvc.crud.controllers.BaseRequest;

public class ProjectsRequest extends BaseRequest {

    private List<Project> projects = new ArrayList<>();

    public ProjectsRequest() {
    }

    public List<Project> getProjects() {
        return projects;
    }

    public void setProjects(List<Project> projects) {
        this.projects = projects;
    }

}
