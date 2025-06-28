package com.samanthamaiaduarte.todoapi.controller;

import com.samanthamaiaduarte.todoapi.domain.user.*;
import com.samanthamaiaduarte.todoapi.service.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
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

@Tag(name = "Users authentication/authorization", description = "Permits to create a new user and generates a token")
@RestController
@RequestMapping("/auth")
public class AuthenticationController {
    @Autowired
    private AuthenticationService authenticationService;

    @Operation(description = "Generate a token to the informed credentials")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", ref = "200login"),
            @ApiResponse(responseCode = "400", ref = "400"),
            @ApiResponse(responseCode = "401", ref = "401login"),
            @ApiResponse(responseCode = "415", ref = "415")
    })
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody AuthenticationDTO data) {
        LoginResponseDTO token = authenticationService.login(data.login(), data.password());
        return ResponseEntity.ok(token);
    }

    @Operation(description = "Create a new user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", ref = "201"),
            @ApiResponse(responseCode = "400", ref = "400"),
            @ApiResponse(responseCode = "409", ref = "409"),
            @ApiResponse(responseCode = "415", ref = "415")
    })
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterDTO data) {
        authenticationService.register(data, UserRole.USER);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
