package com.example.demo.controllers;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.demo.exceptions.*;
import com.example.demo.security.UserData;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;

@RestController
@RequestMapping("/api/user")
public class UserController {

    public static final Logger log = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private BCryptPasswordEncoder bcryptPasswordEncoder;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/id/{id}")
    public ResponseEntity<User> findById(@PathVariable Long id) {
        return ResponseEntity.of(userRepository.findById(id));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{username}")
    public ResponseEntity<User> findByUsername(@PathVariable String username) {
        return ResponseEntity.of(userRepository.findByUsername(username));
    }

    @GetMapping("/find")
    public ResponseEntity<User> find(Authentication authentication) {
		User user = ((UserData) authentication.getPrincipal()).getUser();
        return ResponseEntity.ok(user);
    }

    @PostMapping("/create")
    public ResponseEntity<User> createUser(@RequestBody CreateUserRequest createUserRequest) {
        validateUser(createUserRequest);
        User user = new User();
        user.setUsername(createUserRequest.getUsername());
        user.setPassword(bcryptPasswordEncoder.encode(createUserRequest.getPassword()));
        Cart cart = new Cart();
        cartRepository.save(cart);
        user.setCart(cart);
        userRepository.save(user);

        /**
         * Logging the onboarding of users
         */
        log.info("A new user has been on boarded. Username - {}.", user.getUsername());

        return ResponseEntity.ok(user);
    }

    private void validateUser(CreateUserRequest user) {
        if (user.getUsername() == null) {
            throw new UsernameIsRequiredException();
        }
        if (user.getPassword() == null) {
            throw new PasswordIsRequiredException();
        }
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new UsernameAlreadyExistsException();
        }
        if (user.getPassword().length() < 8) {
            throw new PasswordSizeLesserThanExpectedException();
        }
        if (user.getPassword().length() > 30) {
            throw new PasswordSizeGreaterThanExpectedException();
        }
        if (!user.getPassword().equals(user.getConfirmPassword())) {
            throw new PasswordConfirmationFailedException();
        }
    }

}
