package com.samanthamaiaduarte.todoapi.infra.exceptionhandler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.samanthamaiaduarte.todoapi.exception.ExceptionHandlerDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    @Override
    public void handle(
            HttpServletRequest request,
            HttpServletResponse response,
            AccessDeniedException accessDeniedException) throws IOException {

        int statusCode = HttpServletResponse.SC_FORBIDDEN;
        HttpStatus status = HttpStatus.FORBIDDEN;
        String message = "Access denied: " + accessDeniedException.getMessage();

        ExceptionHandlerDTO responseBody = new ExceptionHandlerDTO(statusCode, status, LocalDateTime.now(), message);

        response.setStatus(statusCode);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8");

        objectMapper.writeValue(response.getOutputStream(), responseBody);
    }
}
