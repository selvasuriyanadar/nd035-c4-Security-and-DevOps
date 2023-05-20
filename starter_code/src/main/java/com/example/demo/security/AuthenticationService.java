package com.example.demo.security;

import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.UserRepository;

import org.springframework.stereotype.Service;
import org.springframework.security.authentication.*;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.*;

@Service
public class AuthenticationService implements AuthenticationProvider {

    private UserDetailsServiceImpl userDetailsServiceImpl;

    public AuthenticationService(UserDetailsServiceImpl userDetailsServiceImpl) {
        this.userDetailsServiceImpl = userDetailsServiceImpl;
    }

    public static class AuthenticationFailedException extends AuthenticationException {
        public AuthenticationFailedException() {
            super("Failed to authenticate User. User Name or Password is not correct.");
        }
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        final String username = authentication.getName();
        final String password = authentication.getCredentials().toString();

        try {
            final UserData user = userDetailsServiceImpl.loadUser(username);

            if (!(new BCryptPasswordEncoder().matches(password, user.getPassword()))) {
                throw new AuthenticationFailedException();
            }

            return new TokenBasedAuthentication(user);
        }
        catch (UsernameNotFoundException e) {
            throw new AuthenticationFailedException();
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }

}
