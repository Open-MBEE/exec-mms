package org.openmbee.sdvc.crud.controllers.projects;

import java.util.ArrayList;
import java.util.List;
import org.openmbee.sdvc.core.domains.Project;
import org.openmbee.sdvc.crud.controllers.BaseResponse;
import org.openmbee.sdvc.crud.controllers.Constants;

public class ProjectsResponse extends BaseResponse {

    public ProjectsResponse() {
        this.put(Constants.PROJECT_KEY, new ArrayList<ProjectJson>());
    }
    public List<ProjectJson> getProjects() {
        return (List<ProjectJson>) this.get(Constants.PROJECT_KEY);
    }

    public void setProjects(List<ProjectJson> projects) {
        this.put(Constants.PROJECT_KEY, projects);
    }

}
