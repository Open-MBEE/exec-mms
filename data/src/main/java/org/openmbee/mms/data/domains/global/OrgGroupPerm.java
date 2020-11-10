package org.openmbee.mms.data.domains.global;

import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "org_group_perms",
    indexes = {
        @Index(columnList = "organization_id"),
        @Index(columnList = "organization_id,group_id")
    })
public class OrgGroupPerm extends Base {

    @ManyToOne
    private Organization organization;

    @ManyToOne
    private Group group;

    @ManyToOne
    private Role role;

    public OrgGroupPerm() {}

    public OrgGroupPerm(Organization org, Group u, Role r) {
        this.organization = org;
        this.group = u;
        this.role = r;
    }

    public Organization getOrganization() {
        return organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
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
}
