package com.samanthamaiaduarte.todoapi.exception;

public class TaskNotFoundException extends RuntimeException {

    public TaskNotFoundException () { super("Task not found or doesn't belong to this user."); }

    public TaskNotFoundException (String message) { super(message); }
}
