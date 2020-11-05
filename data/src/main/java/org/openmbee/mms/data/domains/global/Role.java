package org.openmbee.mms.data.domains.global;

import java.util.Collection;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "roles")
public class Role extends Base {

    @Column(unique = true)
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
    private Set<Privilege> privileges;

    public Role() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Privilege> getPrivileges() {
        return privileges;
    }

    public void setPrivileges(Set<Privilege> privileges) {
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
}
