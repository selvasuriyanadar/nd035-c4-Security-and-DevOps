package com.example.demo.security;

import java.util.Collections;
import java.util.Collection;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.GrantedAuthority;

import com.example.demo.model.persistence.User;

public class UserData implements UserDetails {

    private final User user;

    private final Collection<GrantedAuthority> authorities;
    private final String password;
    private final String username;

    public UserData(User user) {
        this.user = user;

        this.authorities = Collections.emptyList();
        this.password = user.getPassword();
        this.username = user.getUsername();
    }

    public User getUser() {
        return this.user;
    }

    public Collection<GrantedAuthority> getAuthorities() {
        return this.authorities;
    }

    public String getPassword() {
        return this.password;
    }

    public String getUsername() {
        return this.username;
    }

    public boolean isAccountNonExpired() {
        return true;
    }

    public boolean isAccountNonLocked() {
        return true;
    }

    public boolean isCredentialsNonExpired() {
        return true;
    }

    public boolean isEnabled() {
        return true;
    }

}
