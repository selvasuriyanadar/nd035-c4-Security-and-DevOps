package com.example.demo.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "Password is required.")
public class PasswordIsRequiredException extends RuntimeException {

    public PasswordIsRequiredException() {
    }

    public PasswordIsRequiredException(String message) {
        super(message);
    }

}
