package com.samanthamaiaduarte.todoapi.exception;


import org.springframework.security.core.AuthenticationException;

public class ApiTokenExpiredException extends AuthenticationException {

        public ApiTokenExpiredException(String message) { super(message); }

        public ApiTokenExpiredException (AuthenticationException exception) { super("Token has expired.", exception); }

        public ApiTokenExpiredException(String message, AuthenticationException exception) { super(message, exception); }
}
