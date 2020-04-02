package org.openmbee.sdvc.data.domains.global;

import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrgGroupPerm that = (OrgGroupPerm) o;
        return Objects.equals(getOrganization(), that.getOrganization()) &&
            Objects.equals(getGroup(), that.getGroup()) &&
            Objects.equals(getRole(), that.getRole());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getOrganization(), getGroup(), getRole());
    }
}
