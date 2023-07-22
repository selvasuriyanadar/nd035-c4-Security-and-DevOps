package com.example.demo.controllers;

import com.example.demo.security.UserData;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.requests.CreateUserRequest;
import com.example.demo.model.requests.ModifyCartRequest;

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
import java.math.BigDecimal;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureJsonTesters
public class CartControllerTests {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private JacksonTester<User> userJson;

    @Autowired
    private JacksonTester<CreateUserRequest> userRequestJson;

    @Autowired
    private JacksonTester<UserLoginRequest> loginRequestJson;

    @Autowired
    private JacksonTester<ModifyCartRequest> cartRequestJson;

    @Autowired
    private JacksonTester<Item> itemJson;

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

    private String createUserAndLogin() throws Exception {
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

    private Item getItem(String authToken) throws Exception {
        String response = mvc.perform(
                get(new URI("/api/item/" + 1))
                        .accept(MediaType.APPLICATION_JSON_UTF8)
                        .header("Authorization", authToken)).andReturn().getResponse().getContentAsString();
        return itemJson.parseObject(response);
    }

    private ResultActions addToCart(String authToken, ModifyCartRequest cart) throws Exception {
        return mvc.perform(
                post(new URI("/api/cart/addToCart"))
                        .content(cartRequestJson.write(cart).getJson())
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .accept(MediaType.APPLICATION_JSON_UTF8)
                        .header("Authorization", authToken));
    }

    private ResultActions removeFromCart(String authToken, ModifyCartRequest cart) throws Exception {
        return mvc.perform(
                post(new URI("/api/cart/removeFromCart"))
                        .content(cartRequestJson.write(cart).getJson())
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .accept(MediaType.APPLICATION_JSON_UTF8)
                        .header("Authorization", authToken));
    }

    private ResultActions submitOrder(String authToken) throws Exception {
        return mvc.perform(
                post(new URI("/api/order/submit"))
                        .accept(MediaType.APPLICATION_JSON_UTF8)
                        .header("Authorization", authToken));
    }

    /**
     * When an item of some quantity is added to a cart,
     * it is added quantity times to the cart list.
     * The total price in the cart is set correctly by adding up the prices of all the items in it.
     */
    @Test
    public void addToCartTest() throws Exception {
        String authToken = createUserAndLogin();
        Item item = getItem(authToken);
        ModifyCartRequest cart = new ModifyCartRequest();
        cart.setItemId(item.getId());
        cart.setQuantity(5);
        addToCart(authToken, cart)
                .andExpect(status().isOk())
                .andExpect(jsonPath("items", hasSize(cart.getQuantity())))
                .andExpect(jsonPath("total", is(item.getPrice().multiply(new BigDecimal(cart.getQuantity())).doubleValue())));
    }

    /**
     * An item can be removed from the cart in a set quantity.
     * If the item is not found in the cart an exception is thrown.
     */
    @Test
    public void removeFromCartTest() throws Exception {
        String authToken = createUserAndLogin();
        Item item = getItem(authToken);

        ModifyCartRequest cart = new ModifyCartRequest();
        cart.setItemId(item.getId());
        cart.setQuantity(5);
        addToCart(authToken, cart)
                .andExpect(status().isOk());

        cart = new ModifyCartRequest();
        cart.setItemId(item.getId());
        cart.setQuantity(3);
        removeFromCart(authToken, cart)
                .andExpect(status().isOk());

        cart = new ModifyCartRequest();
        cart.setItemId(item.getId() + 1);
        cart.setQuantity(3);
        removeFromCart(authToken, cart)
                .andExpect(status().isNotFound())
                .andExpect(status().reason(containsString("Item not found")));
    }

    /**
     * creates an order from the current user cart
     */
    @Test
    public void submitOrderTest() throws Exception {
        String authToken = createUserAndLogin();
        Item item = getItem(authToken);

        ModifyCartRequest cart = new ModifyCartRequest();
        cart.setItemId(item.getId());
        cart.setQuantity(5);
        addToCart(authToken, cart)
                .andExpect(status().isOk());

        submitOrder(authToken)
                .andExpect(status().isOk())
                .andExpect(jsonPath("items", hasSize(cart.getQuantity())))
                .andExpect(jsonPath("total", is(item.getPrice().multiply(new BigDecimal(cart.getQuantity())).doubleValue())));
    }

}
