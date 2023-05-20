package com.example.demo.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "Password size lesser than expected.")
public class PasswordSizeLesserThanExpectedException extends RuntimeException {

    public PasswordSizeLesserThanExpectedException() {
    }

    public PasswordSizeLesserThanExpectedException(String message) {
        super(message);
    }

}
