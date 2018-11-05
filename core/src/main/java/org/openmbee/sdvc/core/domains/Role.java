package org.openmbee.sdvc.core.domains;

import java.util.Collection;
import java.util.Set;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "roles")
public class Role extends Base {

    private String name;

    @OneToMany(mappedBy = "role")
    private Collection<UsersProjects> usersProjects;

    @ManyToMany(mappedBy = "roles")
    private Collection<Project> rolesProjects;

    @ManyToMany
    @JoinTable(name = "roles_privileges", joinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "privilege_id", referencedColumnName = "id"))
    private Collection<Privilege> privileges;

    public Role() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Collection<UsersProjects> getUsersProjects() {
        return usersProjects;
    }

    public void setUsersProjects(Collection<UsersProjects> usersProjects) {
        this.usersProjects = usersProjects;
    }

    public Collection<Project> getRolesProjects() {
        return rolesProjects;
    }

    public void setRolesProjects(Set<Project> rolesProjects) {
        this.rolesProjects = rolesProjects;
    }

    public Collection<Privilege> getPrivileges() {
        return privileges;
    }

    public void setPrivileges(Collection<Privilege> privileges) {
        this.privileges = privileges;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || id == null || getClass() != o.getClass()) {
            return false;
        }

        Role node = (Role) o;

        return id.equals(node.id);
    }

}
