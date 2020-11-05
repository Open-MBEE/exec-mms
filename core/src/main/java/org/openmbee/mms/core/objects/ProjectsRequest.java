package org.openmbee.mms.core.objects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

import org.openmbee.mms.json.ProjectJson;

@JsonIgnoreProperties({"source", "comment"})
public class ProjectsRequest extends BaseRequest {

    @Schema(required = true)
    private List<ProjectJson> projects;

    public List<ProjectJson> getProjects() {
        return projects;
    }

    public void setProjects(List<ProjectJson> projects) {
        this.projects = projects;
    }

}
