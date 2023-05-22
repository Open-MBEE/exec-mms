package org.openmbee.mms.data.domains.global;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Collection;
import javax.persistence.*;

    @Entity
    @Table(name = "groups")
    public class Group extends Base {

        public static final String NAME_COLUMN = "name";

        @Column(unique = true)
        private String name;

        private String type;

        @ManyToMany(mappedBy = "groups")
        private Collection<User> users;

        @OneToMany(mappedBy = "group", cascade = CascadeType.REMOVE, orphanRemoval = true)
        private Collection<GroupGroupPerm> groupPerms;

        @OneToMany(mappedBy = "group", cascade = CascadeType.REMOVE, orphanRemoval = true)
        private Collection<GroupUserPerm> userPerms;

        private boolean isPublic;

        public Group() {}
        public Group(String name) {
            this.name = name;
            this.type = "remote";
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }


        public String getType() {
            return this.type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public Collection<User> getUsers() {
            return users;
        }

        public void setUsers(Collection<User> users) {
            this.users = users;
        }

        public Collection<GroupGroupPerm> getGroupPerms() {
            return this.groupPerms;
        }

        public void setGroupPerms(Collection<GroupGroupPerm> groupPerms) {
            this.groupPerms = groupPerms;
        }

        public Collection<GroupUserPerm> getUserPerms() {
            return this.userPerms;
        }

        public void setUserPerms(Collection<GroupUserPerm> userPerms) {
            this.userPerms = userPerms;
        }

        public boolean isPublic() {
            return isPublic;
        }

        public void setPublic(boolean aPublic) {
            isPublic = aPublic;
        }
    }
