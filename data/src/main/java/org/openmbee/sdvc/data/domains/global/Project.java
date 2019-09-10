package org.openmbee.sdvc.data.domains.global;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Collection;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "projects")
public class Project extends Base {

    @JsonProperty("name")
    private String projectName;

    @JsonProperty("id")
    @Column(unique = true)
    private String projectId;

    @JsonProperty("projectType")
    private String projectType;

    private String connectionString;

    @ManyToOne
    @JsonManagedReference
    private Organization organization;

    @OneToMany(mappedBy = "project")
    private Collection<Branch> branches;

    @OneToMany(mappedBy = "project")
    private Collection<Metadata> metadata;

    @OneToMany(mappedBy = "project")
    private Collection<ProjectGroupPerm> groupPerms;

    @OneToMany(mappedBy = "project")
    private Collection<ProjectUserPerm> userPerms;

    private boolean isPublic;

    private boolean inherit;

    public Project() {
    }

    public Project(String projectId, String projectName) {
        this.projectId = projectId;
        this.projectName = projectName;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getProjectType() {
        return projectType;
    }

    public void setProjectType(String projectType) {
        this.projectType = projectType;
    }

    public Organization getOrganization() {
        return organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
    }

    public String getOrgId() {
        return organization.getOrganizationId();
    }

    public String getConnectionString() {
        return connectionString;
    }

    public void setConnectionString(String connectionString) {
        this.connectionString = connectionString;
    }

    public Collection<Metadata> getMetadata() {
        return metadata;
    }

    public void setMetadata(Collection<Metadata> metadata) {
        this.metadata = metadata;
    }

    public Collection<Branch> getBranches() {
        return branches;
    }

    public void setBranches(Collection<Branch> branches) {
        this.branches = branches;
    }

    public Collection<ProjectGroupPerm> getGroupPerms() {
        return groupPerms;
    }

    public void setGroupPerms(Collection<ProjectGroupPerm> groupPerms) {
        this.groupPerms = groupPerms;
    }

    public Collection<ProjectUserPerm> getUserPerms() {
        return userPerms;
    }

    public void setUserPerms(Collection<ProjectUserPerm> userPerms) {
        this.userPerms = userPerms;
    }

    public boolean isInherit() {
        return inherit;
    }

    public void setInherit(boolean inherit) {
        this.inherit = inherit;
    }

    public boolean isPublic() {
        return isPublic;
    }

    public void setPublic(boolean aPublic) {
        isPublic = aPublic;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || id == null || getClass() != o.getClass()) {
            return false;
        }

        Project node = (Project) o;

        return id.equals(node.id);
    }
}
