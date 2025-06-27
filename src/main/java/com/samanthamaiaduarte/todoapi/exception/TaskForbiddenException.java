package com.samanthamaiaduarte.todoapi.exception;

public class TaskForbiddenException extends RuntimeException {

    public TaskForbiddenException () { super("User not allowed to update this task."); }

    public TaskForbiddenException (String message) { super(message); }
}
