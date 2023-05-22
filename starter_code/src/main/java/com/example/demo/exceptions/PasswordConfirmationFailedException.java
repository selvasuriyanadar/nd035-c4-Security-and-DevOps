package com.example.demo.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "Password confirmation failed.")
public class PasswordConfirmationFailedException extends RuntimeException {

    public PasswordConfirmationFailedException() {
    }

    public PasswordConfirmationFailedException(String message) {
        super(message);
    }

}
