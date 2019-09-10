package org.openmbee.sdvc.data.domains.global;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "branch_group_perms")
public class BranchGroupPerm extends Base {

    @ManyToOne
    private Branch branch;

    @ManyToOne
    private Group group;

    @ManyToOne
    private Role role;

    private boolean inherited;

    public Branch getBranch() {
        return branch;
    }

    public void setBranch(Branch branch) {
        this.branch = branch;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
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

        BranchGroupPerm node = (BranchGroupPerm) o;

        return id.equals(node.id);
    }
}
