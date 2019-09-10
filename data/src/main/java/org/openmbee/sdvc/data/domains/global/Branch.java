package org.openmbee.sdvc.data.domains.global;

import java.util.Collection;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "branches")
public class Branch extends Base {

    @ManyToOne
    private Project project;

    private String branchId;

    private boolean inherit;

    @OneToMany(mappedBy = "branch")
    private Collection<BranchGroupPerm> groupPerms;

    @OneToMany(mappedBy = "branch")
    private Collection<BranchUserPerm> userPerms;

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public String getBranchId() {
        return branchId;
    }

    public void setBranchId(String branchId) {
        this.branchId = branchId;
    }

    public Collection<BranchUserPerm> getUserPerms() {
        return userPerms;
    }

    public void setUserPerms(Collection<BranchUserPerm> userPerms) {
        this.userPerms = userPerms;
    }

    public Collection<BranchGroupPerm> getGroupPerms() {
        return groupPerms;
    }

    public void setGroupPerms(Collection<BranchGroupPerm> groupPerms) {
        this.groupPerms = groupPerms;
    }

    public boolean isInherit() {
        return inherit;
    }

    public void setInherit(boolean inherit) {
        this.inherit = inherit;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || id == null || getClass() != o.getClass()) {
            return false;
        }

        Branch node = (Branch) o;

        return id.equals(node.id);
    }
}
