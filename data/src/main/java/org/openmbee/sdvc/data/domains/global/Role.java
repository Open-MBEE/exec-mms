package org.openmbee.sdvc.data.domains.global;

import java.util.Collection;
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
    private Collection<ProjectUserPerm> projectUsers;

    @OneToMany(mappedBy = "role")
    private Collection<ProjectGroupPerm> projectGroups;

    @OneToMany(mappedBy = "role")
    private Collection<OrgUserPerm> orgUsers;

    @OneToMany(mappedBy = "role")
    private Collection<OrgGroupPerm> orgGroups;

    @OneToMany(mappedBy = "role")
    private Collection<BranchUserPerm> branchUsers;

    @OneToMany(mappedBy = "role")
    private Collection<BranchGroupPerm> branchGroups;

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

    public Collection<Privilege> getPrivileges() {
        return privileges;
    }

    public void setPrivileges(Collection<Privilege> privileges) {
        this.privileges = privileges;
    }

    public Collection<ProjectUserPerm> getProjectUsers() {
        return projectUsers;
    }

    public void setProjectUsers(Collection<ProjectUserPerm> projectUsers) {
        this.projectUsers = projectUsers;
    }

    public Collection<ProjectGroupPerm> getProjectGroups() {
        return projectGroups;
    }

    public void setProjectGroups(Collection<ProjectGroupPerm> projectGroups) {
        this.projectGroups = projectGroups;
    }

    public Collection<OrgUserPerm> getOrgUsers() {
        return orgUsers;
    }

    public void setOrgUsers(Collection<OrgUserPerm> orgUsers) {
        this.orgUsers = orgUsers;
    }

    public Collection<OrgGroupPerm> getOrgGroups() {
        return orgGroups;
    }

    public void setOrgGroups(Collection<OrgGroupPerm> orgGroups) {
        this.orgGroups = orgGroups;
    }

    public Collection<BranchUserPerm> getBranchUsers() {
        return branchUsers;
    }

    public void setBranchUsers(Collection<BranchUserPerm> branchUsers) {
        this.branchUsers = branchUsers;
    }

    public Collection<BranchGroupPerm> getBranchGroups() {
        return branchGroups;
    }

    public void setBranchGroups(Collection<BranchGroupPerm> branchGroups) {
        this.branchGroups = branchGroups;
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
