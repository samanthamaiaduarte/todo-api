package com.samanthamaiaduarte.todoapi.infra.exceptionhandler;

import com.samanthamaiaduarte.todoapi.exception.*;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CustomExceptionHandler {

    @ExceptionHandler({TaskNotFoundException.class})
    public ResponseEntity<ExceptionHandlerDTO> taskNotFoundHandler(RuntimeException exception) {
        ExceptionHandlerDTO response = new ExceptionHandlerDTO(HttpStatus.NOT_FOUND.value(), HttpStatus.NOT_FOUND, LocalDateTime.now(), exception.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler({UserNotFoundException.class})
    public ResponseEntity<ExceptionHandlerDTO> userNotFoundHandler(RuntimeException exception) {
        ExceptionHandlerDTO response = new ExceptionHandlerDTO(HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED, LocalDateTime.now(), exception.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ExceptionHandlerDTO> userAlreadyExistsHandler(UserAlreadyExistsException exception) {
        ExceptionHandlerDTO response = new ExceptionHandlerDTO(HttpStatus.CONFLICT.value(), HttpStatus.CONFLICT, LocalDateTime.now(), exception.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }
}
