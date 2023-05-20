package com.example.demo.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.authentication.AbstractAuthenticationToken;

public class TokenBasedAuthentication extends AbstractAuthenticationToken {

    private final UserData principal;

    public TokenBasedAuthentication(UserData userData) {
        super(userData.getAuthorities());
        this.principal = userData;
    }

    @Override
    public boolean isAuthenticated() {
        return true;
    }

    @Override
    public UserDetails getPrincipal() {
        return this.principal;
    }

    public Object getCredentials() {
        return null;
    }

    public UserData getUserData() {
        return this.principal;
    }

}
