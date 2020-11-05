package org.openmbee.mms.data.domains.global;

import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "org_user_perms",
    indexes = {
        @Index(columnList = "organization_id"),
        @Index(columnList = "organization_id,user_id")
    })
public class OrgUserPerm extends Base {

    @ManyToOne
    private Organization organization;

    @ManyToOne
    private User user;

    @ManyToOne
    private Role role;

    public OrgUserPerm() {}

    public OrgUserPerm(Organization org, User u, Role r) {
        this.organization = org;
        this.user = u;
        this.role = r;
    }

    public Organization getOrganization() {
        return organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
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
}
