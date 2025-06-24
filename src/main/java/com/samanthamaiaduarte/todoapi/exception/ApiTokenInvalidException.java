package com.samanthamaiaduarte.todoapi.exception;

import org.springframework.security.core.AuthenticationException;

public class ApiTokenInvalidException extends AuthenticationException {

    public ApiTokenInvalidException(String message) { super(message); }

    public ApiTokenInvalidException(AuthenticationException exception) { super("Invalid token.", exception); }

    public ApiTokenInvalidException(String message, AuthenticationException exception) { super(message, exception); }

}
