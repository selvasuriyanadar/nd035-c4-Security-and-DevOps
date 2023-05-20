package com.example.demo.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "Username is required.")
public class UsernameIsRequiredException extends RuntimeException {

    public UsernameIsRequiredException() {
    }

    public UsernameIsRequiredException(String message) {
        super(message);
    }

}
