package com.samanthamaiaduarte.todoapi.controller;

import com.samanthamaiaduarte.todoapi.domain.user.RegisterDTO;
import com.samanthamaiaduarte.todoapi.domain.user.UserRole;
import com.samanthamaiaduarte.todoapi.exception.ExceptionHandlerDTO;
import com.samanthamaiaduarte.todoapi.service.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Admin users", description = "New admin user creation endpoint")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/admin")
public class AuthenticationAdminController {
    @Autowired
    private AuthenticationService authenticationService;

    @Operation(description = "Create a new user with ADMIN role. This endpoint requires a Admin user token.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", ref = "201"),
            @ApiResponse(responseCode = "400", ref = "400"),
            @ApiResponse(responseCode = "401", ref = "401"),
            @ApiResponse(responseCode = "403", ref = "403"),
            @ApiResponse(responseCode = "409", ref = "409"),
            @ApiResponse(responseCode = "415", ref = "415")
    })
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterDTO data) {
        authenticationService.register(data, UserRole.ADMIN);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
