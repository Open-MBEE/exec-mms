package org.openmbee.sdvc.crud.controllers.projects;

import java.util.List;

import org.openmbee.sdvc.crud.controllers.BaseRequest;
import org.openmbee.sdvc.crud.controllers.Constants;

public class ProjectsRequest extends BaseRequest {

    public List<ProjectJson> getProjects() {
        return (List<ProjectJson>) this.get(Constants.PROJECT_KEY);
    }
    public void setProjects(List<ProjectJson> projects) {
        this.put(Constants.PROJECT_KEY, projects);
    }


}
