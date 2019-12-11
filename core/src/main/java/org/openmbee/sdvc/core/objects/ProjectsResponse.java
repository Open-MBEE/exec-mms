package org.openmbee.sdvc.core.objects;

import java.util.ArrayList;
import java.util.List;

import org.openmbee.sdvc.core.config.Constants;
import org.openmbee.sdvc.json.ProjectJson;

public class ProjectsResponse extends BaseResponse<ProjectsResponse> {

    private List<ProjectJson> projects;

    public ProjectsResponse() {
        this.projects = new ArrayList<>();
    }

    public List<ProjectJson> getProjects() {
        return projects;
    }

    public ProjectsResponse setProjects(List<ProjectJson> projects) {
        this.projects = projects;
        return this;
    }
}
