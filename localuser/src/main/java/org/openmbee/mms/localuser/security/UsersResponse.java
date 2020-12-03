package org.openmbee.mms.localuser.security;

import java.util.List;
import org.openmbee.mms.data.domains.global.User;

public class UsersResponse {

    private List<User> users;

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }
}
