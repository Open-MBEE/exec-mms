package org.openmbee.mms.data.domains.global;

import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "branch_group_perms",
    indexes = {
        @Index(columnList = "branch_id"),
        @Index(columnList = "branch_id,group_id"),
        @Index(columnList = "branch_id,inherited")
    })
public class BranchGroupPerm extends Base {

    @ManyToOne
    private Branch branch;

    @ManyToOne
    private Group group;

    @ManyToOne
    private Role role;

    private boolean inherited;

    public BranchGroupPerm() {}

    public BranchGroupPerm(Branch b, Group u, Role r, boolean inherited) {
        this.branch = b;
        this.group = u;
        this.role = r;
        this.inherited = inherited;
    }

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
}
