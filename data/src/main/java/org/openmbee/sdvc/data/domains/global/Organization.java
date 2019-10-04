package org.openmbee.sdvc.data.domains.global;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Collection;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "organizations")
public class Organization extends Base {

    @JsonProperty("name")
    private String organizationName;

    @JsonProperty("id")
    @Column(unique = true)
    private String organizationId;

    @OneToMany(mappedBy = "organization")
    @JsonBackReference
    private Collection<Project> projects;

    @OneToMany(mappedBy = "organization")
    private Collection<OrgGroupPerm> groupPerms;

    @OneToMany(mappedBy = "organization")
    private Collection<OrgUserPerm> userPerms;

    private boolean isPublic;

    public Organization() {
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    public String getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(String organizationId) {
        this.organizationId = organizationId;
    }

    public Collection<Project> getProjects() {
        return projects;
    }

    public void setProjects(Collection<Project> projects) {
        this.projects = projects;
    }

    public Collection<OrgGroupPerm> getGroupPerms() {
        return groupPerms;
    }

    public void setGroupPerms(Collection<OrgGroupPerm> groupPerms) {
        this.groupPerms = groupPerms;
    }

    public Collection<OrgUserPerm> getUserPerms() {
        return userPerms;
    }

    public void setUserPerms(Collection<OrgUserPerm> userPerms) {
        this.userPerms = userPerms;
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

        Organization node = (Organization) o;

        return id.equals(node.id);
    }

}
