package org.openmbee.mms.twc.security;

import org.openmbee.mms.core.config.AuthorizationConstants;
import org.openmbee.mms.data.domains.global.Group;
import org.openmbee.mms.data.domains.global.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;

class TwcUserDetails implements UserDetails {

    private final User user;

    public TwcUserDetails(User user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<Group> groups = user.getGroups();
        Collection<GrantedAuthority> authorities = new ArrayList<>();

        if (groups != null) {
            for (Group group : groups) {
                authorities.add(new SimpleGrantedAuthority(group.getName()));
            }
        }
        if (user.isAdmin()) {
            authorities.add(new SimpleGrantedAuthority(AuthorizationConstants.MMSADMIN));
        }
        authorities.add(new SimpleGrantedAuthority(AuthorizationConstants.EVERYONE));
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
