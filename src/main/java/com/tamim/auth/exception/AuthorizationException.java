package com.tamim.auth.exception;

import org.springframework.http.HttpStatus;

public class AuthorizationException extends ApiException {

    public AuthorizationException(String message) {
        super(message, HttpStatus.FORBIDDEN, "ACCESS_DENIED");
    }
}
