package com.samanthamaiaduarte.todoapi.exception;

public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException() { super("User not found or wrong password."); }

    public UserNotFoundException(String message) { super(message); }
}
