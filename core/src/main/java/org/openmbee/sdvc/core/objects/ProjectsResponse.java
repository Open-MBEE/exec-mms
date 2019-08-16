package org.openmbee.sdvc.core.objects;

import java.util.ArrayList;
import java.util.List;

import org.openmbee.sdvc.core.config.Constants;
import org.openmbee.sdvc.json.ProjectJson;

public class ProjectsResponse extends BaseResponse<ProjectsResponse> {

    public ProjectsResponse() {
        this.put(Constants.PROJECT_KEY, new ArrayList<ProjectJson>());
    }

    public List<ProjectJson> getProjects() {
        return (List<ProjectJson>) this.get(Constants.PROJECT_KEY);
    }

    public ProjectsResponse setProjects(List<ProjectJson> projects) {
        this.put(Constants.PROJECT_KEY, projects);
        return this;
    }

}
