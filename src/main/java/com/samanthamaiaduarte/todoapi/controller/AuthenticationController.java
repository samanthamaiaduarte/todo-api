package com.samanthamaiaduarte.todoapi.controller;

import com.samanthamaiaduarte.todoapi.domain.user.*;
import com.samanthamaiaduarte.todoapi.exception.UserAlreadyExistsException;
import com.samanthamaiaduarte.todoapi.repository.UserRepository;
import com.samanthamaiaduarte.todoapi.service.AuthenticationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {
    @Autowired
    private AuthenticationService authenticationService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody AuthenticationDTO data) {
        LoginResponseDTO token = authenticationService.login(data.login(), data.password());
        return ResponseEntity.ok(token);
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponseDTO> register(@Valid @RequestBody RegisterDTO data) {
        RegisterResponseDTO response = authenticationService.register(data);
        return ResponseEntity.ok(response);
    }

}
