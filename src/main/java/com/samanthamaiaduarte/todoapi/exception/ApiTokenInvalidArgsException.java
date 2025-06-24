package com.samanthamaiaduarte.todoapi.exception;

public class ApiTokenInvalidArgsException extends RuntimeException {

    public ApiTokenInvalidArgsException() { super("Invalid information."); }

    public ApiTokenInvalidArgsException(String message) { super(message); }
}

