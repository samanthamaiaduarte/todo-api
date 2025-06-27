package com.samanthamaiaduarte.todoapi.exception;

public class TaskNotFoundException extends RuntimeException {

    public TaskNotFoundException () { super("Task not found."); }

    public TaskNotFoundException (String message) { super(message); }
}
