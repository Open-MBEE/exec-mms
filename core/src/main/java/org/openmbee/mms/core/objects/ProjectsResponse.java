package org.openmbee.mms.core.objects;

import java.util.ArrayList;
import java.util.List;

import org.openmbee.mms.json.ProjectJson;

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
