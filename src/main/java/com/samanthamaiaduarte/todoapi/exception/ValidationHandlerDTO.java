package com.samanthamaiaduarte.todoapi.exception;

import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.Map;

public record ValidationHandlerDTO(Integer statusCode, HttpStatus status, LocalDateTime timestamp, Map<String, String> errorMessage) {
}
