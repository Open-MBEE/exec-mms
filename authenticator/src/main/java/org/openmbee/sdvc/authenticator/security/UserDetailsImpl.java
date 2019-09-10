package org.openmbee.sdvc.authenticator.security;

import java.util.ArrayList;
import java.util.Collection;
import org.openmbee.sdvc.data.domains.global.Privilege;
import org.openmbee.sdvc.data.domains.global.Role;
import org.openmbee.sdvc.data.domains.global.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class UserDetailsImpl implements UserDetails {

    private final User user;

    public UserDetailsImpl(User user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<Role> roles = null;//user.getRoles();
        Collection<Privilege> privileges = new ArrayList<>();
        Collection<GrantedAuthority> authorities = new ArrayList<>();

        if (roles != null) {
            for (Role role : roles) {
                privileges.addAll(role.getPrivileges());
            }
        }

        for (Privilege privilege : privileges) {
            authorities.add(new SimpleGrantedAuthority(privilege.getName()));
        }

        return authorities;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return user.getEnabled();
    }

    public User getUser() {
        return user;
    }

}
