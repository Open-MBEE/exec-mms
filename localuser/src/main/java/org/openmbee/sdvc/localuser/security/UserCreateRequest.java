package org.openmbee.sdvc.localuser.security;

import java.io.Serializable;

public class UserCreateRequest implements Serializable {

    private static final long serialVersionUID = -849270125785286560L;

    private String username;
    private String password;

    public UserCreateRequest() {
        super();
    }

    public UserCreateRequest(String username, String password) {
        this.setUsername(username);
        this.setPassword(password);
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
