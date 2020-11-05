package org.openmbee.mms.data.domains.global;

import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "project_user_perms",
    indexes = {
        @Index(columnList = "project_id"),
        @Index(columnList = "project_id,user_id"),
        @Index(columnList = "project_id,inherited")
    })
public class ProjectUserPerm extends Base {

    @ManyToOne
    private Project project;

    @ManyToOne
    private User user;

    @ManyToOne
    private Role role;

    private boolean inherited;

    public ProjectUserPerm() {}

    public ProjectUserPerm(Project p, User u, Role r, boolean inherited) {
        this.project = p;
        this.user = u;
        this.role = r;
        this.inherited = inherited;
    }

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
}
