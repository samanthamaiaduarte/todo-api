package com.samanthamaiaduarte.todoapi.infra.exceptionhandler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.samanthamaiaduarte.todoapi.exception.ApiTokenExpiredException;
import com.samanthamaiaduarte.todoapi.exception.ApiTokenInvalidException;
import com.samanthamaiaduarte.todoapi.exception.ExceptionHandlerDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException) throws IOException {

        Throwable exception = authException.getCause() != null ? authException.getCause() : authException;

        int status = HttpServletResponse.SC_FORBIDDEN;
        HttpStatus httpStatus = HttpStatus.FORBIDDEN;
        String message = exception.getMessage();

        if (exception instanceof ApiTokenExpiredException || exception instanceof ApiTokenInvalidException) {
            status = HttpServletResponse.SC_UNAUTHORIZED;
            httpStatus = HttpStatus.UNAUTHORIZED;
            message = exception.getMessage();
        }

        ExceptionHandlerDTO responseBody = new ExceptionHandlerDTO(status, httpStatus, LocalDateTime.now(), message);

        response.setStatus(status);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE + ";charset=UTF-8");

        objectMapper.writeValue(response.getOutputStream(), responseBody);
        response.flushBuffer();
    }
}
