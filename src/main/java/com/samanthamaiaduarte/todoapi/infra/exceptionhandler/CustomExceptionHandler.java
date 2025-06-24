package com.samanthamaiaduarte.todoapi.infra.exceptionhandler;

import com.samanthamaiaduarte.todoapi.exception.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CustomExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(CustomExceptionHandler.class);

    @ExceptionHandler(TaskNotFoundException.class)
    public ResponseEntity<ExceptionHandlerDTO> taskNotFoundHandler(TaskNotFoundException exception) {
        logger.warn("Task not found exception: {}", exception.getMessage());

        ExceptionHandlerDTO response = new ExceptionHandlerDTO(HttpStatus.NOT_FOUND.value(), HttpStatus.NOT_FOUND, LocalDateTime.now(), exception.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ExceptionHandlerDTO> userNotFoundHandler(UserNotFoundException exception) {
        logger.warn("User not found exception: {}", exception.getMessage());

        ExceptionHandlerDTO response = new ExceptionHandlerDTO(HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED, LocalDateTime.now(), exception.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ExceptionHandlerDTO> userAlreadyExistsHandler(UserAlreadyExistsException exception) {
        logger.warn("User already exists exception: {}", exception.getMessage());

        ExceptionHandlerDTO response = new ExceptionHandlerDTO(HttpStatus.CONFLICT.value(), HttpStatus.CONFLICT, LocalDateTime.now(), exception.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler(ApiTokenCreationException.class)
    public ResponseEntity<ExceptionHandlerDTO> tokenCreationHandler(ApiTokenCreationException exception) {
        logger.error("Token creation exception: ", exception);

        ExceptionHandlerDTO response = new ExceptionHandlerDTO(HttpStatus.INTERNAL_SERVER_ERROR.value(), HttpStatus.INTERNAL_SERVER_ERROR, LocalDateTime.now(), exception.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    @ExceptionHandler(ApiTokenInvalidArgsException.class)
    public ResponseEntity<ExceptionHandlerDTO> tokenInvalidHandler(ApiTokenInvalidArgsException exception) {
        logger.warn("Invalid token arguments: {}", exception.getMessage());

        ExceptionHandlerDTO response = new ExceptionHandlerDTO(HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST, LocalDateTime.now(), exception.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
}
