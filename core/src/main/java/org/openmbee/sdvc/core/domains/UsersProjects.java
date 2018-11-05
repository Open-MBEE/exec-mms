package org.openmbee.sdvc.core.domains;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "users_projects")
public class UsersProjects extends Base {

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "project_id")
    private Project project;

    @ManyToOne
    @JoinColumn(name = "role_id")
    private Role role;

    public UsersProjects() {
    }

    public UsersProjects(Project project, User user) {
        this.project = project;
        this.user = user;
    }

    public UsersProjects(Project project, User user, Role role) {
        this.project = project;
        this.user = user;
        this.role = role;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || id == null || getClass() != o.getClass()) {
            return false;
        }

        UsersProjects node = (UsersProjects) o;

        return id.equals(node.id);
    }

}
