package com.samanthamaiaduarte.todoapi.exception;

import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

public record ExceptionHandlerDTO(Integer statusCode, HttpStatus status, LocalDateTime timestamp, String errorMessage) {
}
