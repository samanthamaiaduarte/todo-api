package com.samanthamaiaduarte.todoapi.infra.exceptionhandler;

import com.samanthamaiaduarte.todoapi.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;

@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ExceptionHandlerDTO> userNotFoundHandler(UserNotFoundException exception) {
        ExceptionHandlerDTO response = new ExceptionHandlerDTO(HttpStatus.NOT_FOUND.value(), HttpStatus.NOT_FOUND, LocalDateTime.now(), exception.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ExceptionHandlerDTO> userAlreadyExistsHandler(UserAlreadyExistsException exception) {
        ExceptionHandlerDTO response = new ExceptionHandlerDTO(HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST, LocalDateTime.now(), exception.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(InvalidUsernameException.class)
    public ResponseEntity<ExceptionHandlerDTO> invalidUsernameHandler(InvalidUsernameException exception) {
        ExceptionHandlerDTO response = new ExceptionHandlerDTO(HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST, LocalDateTime.now(), exception.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(InvalidPasswordException.class)
    public ResponseEntity<ExceptionHandlerDTO> invalidPasswordHandler(InvalidPasswordException exception) {
        ExceptionHandlerDTO response = new ExceptionHandlerDTO(HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST, LocalDateTime.now(), exception.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(Throwable.class)
    public ResponseEntity<ExceptionHandlerDTO> unexpectedHandler(Throwable exception) {
        ExceptionHandlerDTO response = new ExceptionHandlerDTO(HttpStatus.INTERNAL_SERVER_ERROR.value(), HttpStatus.INTERNAL_SERVER_ERROR, LocalDateTime.now(), "Unexpected server error: " + exception.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
