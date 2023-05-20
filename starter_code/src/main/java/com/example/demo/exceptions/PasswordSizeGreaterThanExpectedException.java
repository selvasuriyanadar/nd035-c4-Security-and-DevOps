package com.example.demo.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "Password size greater than expected.")
public class PasswordSizeGreaterThanExpectedException extends RuntimeException {

    public PasswordSizeGreaterThanExpectedException() {
    }

    public PasswordSizeGreaterThanExpectedException(String message) {
        super(message);
    }

}
