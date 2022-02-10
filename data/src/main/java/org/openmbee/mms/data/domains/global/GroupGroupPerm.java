package org.openmbee.mms.data.domains.global;

import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "group_group_perms",
    indexes = {
        @Index(columnList = "group_id"),
        @Index(columnList = "group_id,group_id")
    })
public class GroupGroupPerm extends Base {

    @ManyToOne
    private Group group;

    @ManyToOne
    private Group groupPerms;

    @ManyToOne
    private Role role;

    public GroupGroupPerm() {}

    public GroupGroupPerm(Group group, Group u, Role r) {
        this.group = group;
        this.groupPerms = u;
        this.role = r;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public Group getGroupPerms() {
        return groupPerms;
    }

    public void setGroupPerms(Group group) {
        this.groupPerms = group;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}
