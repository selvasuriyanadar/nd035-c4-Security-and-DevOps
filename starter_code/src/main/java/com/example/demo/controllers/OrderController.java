package com.example.demo.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.Authentication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.demo.security.UserData;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;

@RestController
@RequestMapping("/api/order")
public class OrderController {

    public static final Logger log = LoggerFactory.getLogger(OrderController.class);

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private OrderRepository orderRepository;
	
	@PostMapping("/submit")
	public ResponseEntity<UserOrder> submit(Authentication authentication) {
		User user = ((UserData) authentication.getPrincipal()).getUser();
		UserOrder order = UserOrder.createFromCart(user.getCart());
        order.setUser(user);
		orderRepository.save(order);

        /**
         * Logging order creations.
         */
        log.info("A new order has been placed by user {}. Order Id - {}", user.getUsername(), order.getId());

		return ResponseEntity.ok(order);
	}
	
	@GetMapping("/history")
	public ResponseEntity<List<UserOrder>> getOrdersForUser(Authentication authentication) {
		User user = ((UserData) authentication.getPrincipal()).getUser();
		return ResponseEntity.ok(orderRepository.findByUser(user));
	}
}
