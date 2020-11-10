package org.openmbee.mms.data.domains.global;

import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "branch_user_perms",
    indexes = {
        @Index(columnList = "branch_id"),
        @Index(columnList = "branch_id,user_id"),
        @Index(columnList = "branch_id,inherited")
    })
public class BranchUserPerm extends Base {

    @ManyToOne
    private Branch branch;

    @ManyToOne
    private User user;

    @ManyToOne
    private Role role;

    private boolean inherited;

    public BranchUserPerm() {}

    public BranchUserPerm(Branch b, User u, Role r, boolean inherited) {
        this.branch = b;
        this.user = u;
        this.role = r;
        this.inherited = inherited;
    }

    public Branch getBranch() {
        return branch;
    }

    public void setBranch(Branch branch) {
        this.branch = branch;
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
