package org.openmbee.sdvc.core.objects;

import java.util.List;

import org.openmbee.sdvc.json.ProjectJson;

public class ProjectsRequest extends BaseRequest {

    private List<ProjectJson> projects;

    public List<ProjectJson> getProjects() {
        return projects;
    }

    public void setProjects(List<ProjectJson> projects) {
        this.projects = projects;
    }


}
