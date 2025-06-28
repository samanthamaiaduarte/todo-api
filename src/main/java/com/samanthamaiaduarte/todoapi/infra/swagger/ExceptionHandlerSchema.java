package com.samanthamaiaduarte.todoapi.infra.swagger;

import java.time.LocalDateTime;

public record ExceptionHandlerSchema (Integer statusCode, String status, LocalDateTime timestamp, String errorMessage) {
}
