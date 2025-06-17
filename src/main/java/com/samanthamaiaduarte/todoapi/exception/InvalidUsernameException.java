package com.samanthamaiaduarte.todoapi.exception;

public class InvalidUsernameException extends RuntimeException{

    public InvalidUsernameException() { super("Invalid username."); }

    public InvalidUsernameException(String message) { super(message); }
}
