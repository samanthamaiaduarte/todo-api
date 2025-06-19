package com.samanthamaiaduarte.todoapi.service;

import com.samanthamaiaduarte.todoapi.domain.user.*;
import com.samanthamaiaduarte.todoapi.exception.UserAlreadyExistsException;
import com.samanthamaiaduarte.todoapi.exception.UserNotFoundException;
import com.samanthamaiaduarte.todoapi.infra.security.TokenService;
import com.samanthamaiaduarte.todoapi.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private TokenService tokenService;
    @Autowired
    private UserRepository repository;


    public LoginResponseDTO login(String username, String password) {
        try {
            var usernamePassword = new UsernamePasswordAuthenticationToken(username, password);
            var auth = authenticationManager.authenticate(usernamePassword);

            return tokenService.generateToken((User) auth.getPrincipal());
        } catch (InternalAuthenticationServiceException | BadCredentialsException exception) {
            throw new UserNotFoundException();
        }
    }

    public void register(RegisterDTO data, UserRole role) {
        if(repository.findByLogin(data.login()) != null) throw new UserAlreadyExistsException();

        String encryptedPassword = new BCryptPasswordEncoder().encode(data.password());
        User newUser = new  User(data.login(), encryptedPassword, role);

        repository.save(newUser);
    }
}
