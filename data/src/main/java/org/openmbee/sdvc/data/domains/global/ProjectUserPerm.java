package org.openmbee.sdvc.data.domains.global;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "project_user_perms")
public class ProjectUserPerm extends Base {

    @ManyToOne
    private Project project;

    @ManyToOne
    private User user;

    @ManyToOne
    private Role role;

    private boolean inherited;

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public boolean isInherited() {
        return inherited;
    }

    public void setInherited(boolean inherited) {
        this.inherited = inherited;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || id == null || getClass() != o.getClass()) {
            return false;
        }

        ProjectUserPerm node = (ProjectUserPerm) o;

        return id.equals(node.id);
    }
}
