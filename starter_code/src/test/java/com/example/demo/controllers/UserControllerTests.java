package com.example.demo.controllers;

import com.example.demo.security.UserData;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.User;
import com.example.demo.model.requests.CreateUserRequest;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.net.URI;
import java.util.*;
import java.time.LocalDateTime;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureJsonTesters
public class UserControllerTests {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private JacksonTester<User> userJson;

    @Autowired
    private JacksonTester<CreateUserRequest> userRequestJson;

    @Autowired
    private JacksonTester<UserLoginRequest> loginRequestJson;

    private ResultActions createUser(CreateUserRequest user) throws Exception {
        return mvc.perform(
                post(new URI("/api/user/create"))
                        .content(userRequestJson.write(user).getJson())
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .accept(MediaType.APPLICATION_JSON_UTF8));
    }

    private ResultActions login(String username, String password) throws Exception {
        return mvc.perform(
                post(new URI("/login"))
                        .content(loginRequestJson.write(new UserLoginRequest(username, password)).getJson())
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .accept(MediaType.APPLICATION_JSON_UTF8));
    }

    public String createUserAndLogin() throws Exception {
        CreateUserRequest user = new CreateUserRequest();
        user.setUsername("Raja");
        user.setPassword("raja@123");
        user.setConfirmPassword("raja@123");
        return createUserAndLogin(user);
    }

    private String createUserAndLogin(CreateUserRequest user) throws Exception {
        createUser(user);
        return login(user.getUsername(), user.getPassword()).andReturn().getResponse().getHeader("Authorization");
    }

    private ResultActions findUser(String authToken) throws Exception {
        return mvc.perform(
                get(new URI("/api/user/find"))
                        .accept(MediaType.APPLICATION_JSON_UTF8)
                        .header("Authorization", authToken));
    }

    /**
     * Creating a user successfully
     */
    @Test
    public void createUser() throws Exception {
        CreateUserRequest user = new CreateUserRequest();
        user.setUsername("Raja");
        user.setPassword("raja@123");
        user.setConfirmPassword("raja@123");
        createUser(user)
                .andExpect(status().isOk());
    }

    /**
     * A valid user shall have a name and a password
     */
    @Test
    public void createUserNullValidation() throws Exception {
        CreateUserRequest user = new CreateUserRequest();
        createUser(user)
                .andExpect(status().isBadRequest())
                .andExpect(status().reason(containsString("Username is required.")));

        user = new CreateUserRequest();
        user.setUsername("Raja");
        createUser(user)
                .andExpect(status().isBadRequest())
                .andExpect(status().reason(containsString("Password is required.")));
    }

    /**
     * User name shall be unique for a user
     */
    @Test
    public void createUserDuplicateValidation() throws Exception {
        CreateUserRequest user = new CreateUserRequest();
        user.setUsername("Raja");
        user.setPassword("raja@123");
        user.setConfirmPassword("raja@123");
        createUser(user)
                .andExpect(status().isOk());

        user = new CreateUserRequest();
        user.setUsername("Raja");
        user.setPassword("raja@duplicate");
        user.setConfirmPassword("raja@duplicate");
        createUser(user)
                .andExpect(status().isBadRequest())
                .andExpect(status().reason(containsString("Username Already exists.")));
    }

    /**
     * A password should be minimum 8 characters and maximum 30 characters long.
     * Confirm password has to match the password.
     */
    @Test
    public void createUserPasswordValidation() throws Exception {
        CreateUserRequest user = new CreateUserRequest();
        user.setUsername("Raja");
        user.setPassword("raja@1");
        user.setConfirmPassword("raja@1");
        createUser(user)
                .andExpect(status().isBadRequest())
                .andExpect(status().reason(containsString("Password size lesser than expected.")));

        user = new CreateUserRequest();
        user.setUsername("Raja");
        user.setPassword("raja@123456789101112131415161718");
        user.setConfirmPassword("raja@123456789101112131415161718");
        createUser(user)
                .andExpect(status().isBadRequest())
                .andExpect(status().reason(containsString("Password size greater than expected.")));

        user = new CreateUserRequest();
        user.setUsername("Raja");
        user.setPassword("raja@123");
        user.setConfirmPassword("raja@321");
        createUser(user)
                .andExpect(status().isBadRequest())
                .andExpect(status().reason(containsString("Password confirmation failed.")));
    }

    /**
     * User is able to login successfully.
     */
    @Test
    public void loginTest() throws Exception {
        CreateUserRequest user = new CreateUserRequest();
        user.setUsername("Raja");
        user.setPassword("raja@123");
        user.setConfirmPassword("raja@123");
        createUser(user)
                .andExpect(status().isOk());

        login(user.getUsername(), user.getPassword())
                .andExpect(status().isOk())
                .andExpect(header().exists("Authorization"))
                .andExpect(header().string("Authorization", containsString("Bearer ")));
    }

    /**
     * User is not logged in for wrong password.
     */
    @Test
    public void loginWithInvalidPasswordTest() throws Exception {
        CreateUserRequest user = new CreateUserRequest();
        user.setUsername("Raja");
        user.setPassword("raja@123");
        user.setConfirmPassword("raja@123");
        createUser(user)
                .andExpect(status().isOk());

        login(user.getUsername(), "raja@321")
                .andExpect(status().isUnauthorized());
    }

    /**
     * User is not logged in for invalid username.
     */
    @Test
    public void loginWithInvalidUserTest() throws Exception {
        login("Raja", "raja@123")
                .andExpect(status().isUnauthorized());
    }

    /**
     * Find user returns the current user profile
     */
    @Test
    public void findUserTest() throws Exception {
        CreateUserRequest user = new CreateUserRequest();
        user.setUsername("Raja");
        user.setPassword("raja@123");
        user.setConfirmPassword("raja@123");
        String authToken = createUserAndLogin(user);

        findUser(authToken)
                .andExpect(status().isOk())
                .andExpect(jsonPath("username", is(user.getUsername())));
    }

}
