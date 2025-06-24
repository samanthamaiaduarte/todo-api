package com.samanthamaiaduarte.todoapi.exception;

public class ApiTokenCreationException extends RuntimeException {

    public ApiTokenCreationException () { super("Error generating token."); }

    public ApiTokenCreationException (String message) { super(message); }
}
