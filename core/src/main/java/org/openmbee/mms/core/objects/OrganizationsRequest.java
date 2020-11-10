package org.openmbee.mms.core.objects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

import org.openmbee.mms.json.OrgJson;

@JsonIgnoreProperties({"source", "comment"})
public class OrganizationsRequest extends BaseRequest {

    @Schema(required = true)
    private List<OrgJson> orgs;

    public List<OrgJson> getOrgs() {
        return orgs;
    }

    public void setOrgs(List<OrgJson> orgs) {
        this.orgs = orgs;
    }

}
