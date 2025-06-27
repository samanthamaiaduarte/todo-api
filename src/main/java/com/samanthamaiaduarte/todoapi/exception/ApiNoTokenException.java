package com.samanthamaiaduarte.todoapi.exception;

import org.springframework.security.core.AuthenticationException;

public class ApiNoTokenException  extends AuthenticationException {

    public ApiNoTokenException () { super("Authentication is required to access this resource."); }

    public ApiNoTokenException(String message) { super(message); }

    public ApiNoTokenException (AuthenticationException exception) { super("Authentication is required to access this resource.", exception); }

    public ApiNoTokenException(String message, AuthenticationException exception) { super(message, exception); }
}
