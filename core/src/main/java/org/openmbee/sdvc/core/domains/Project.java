package org.openmbee.sdvc.core.domains;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Collection;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
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

    private String connectionString;

    @OneToMany(mappedBy = "user")
    private Collection<UsersProjects> users;

    @ManyToOne
    private Organization organization;

    @ManyToMany
    @JoinTable(name = "roles_projects", joinColumns = @JoinColumn(name = "project_id"), inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"))
    @JsonIgnore
    private Collection<Role> roles;

    @ManyToMany
    @JoinTable(name = "projects_metadata", joinColumns = @JoinColumn(name = "project_id"), inverseJoinColumns = @JoinColumn(name = "metadata_id", referencedColumnName = "id"))
    private Collection<Metadata> metadata;

    public Project() {
    }

    public Project(String projectId, String projectName) {
        this.projectId = projectId;
        this.projectName = projectName;
    }

    public Project(String projectId, String projectName, Set<UsersProjects> users,
        Set<Role> roles) {
        this.projectId = projectId;
        this.projectName = projectName;
        this.users = users;
        this.roles = roles;
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

    public Collection<UsersProjects> getUsers() {
        return users;
    }

    public void setUsers(Set<UsersProjects> users) {
        this.users = users;
    }

    public void addUser(User user) {
        UsersProjects usersProjects = new UsersProjects(this, user);
        users.add(usersProjects);
    }

    public void removeUser(User user) {
        for (UsersProjects usersProjects : users) {
            if (usersProjects.getUser().equals(user)) {
                users.remove(usersProjects);
            }
        }
    }

    public Organization getOrganization() {
        return organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
    }

    public Collection<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public Role getRoleForUser(User user) {
        for (UsersProjects usersProjects : users) {
            if (usersProjects.getUser().equals(user)) {
                return usersProjects.getRole();
            }
        }

        return null;
    }

    public String getConnectionString() {
        return connectionString;
    }

    public void setConnectionString(String connectionString) {
        this.connectionString = connectionString;
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
