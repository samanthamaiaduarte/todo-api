package com.samanthamaiaduarte.todoapi.service;

import com.samanthamaiaduarte.todoapi.domain.user.LoginResponseDTO;
import com.samanthamaiaduarte.todoapi.domain.user.RegisterDTO;
import com.samanthamaiaduarte.todoapi.domain.user.User;
import com.samanthamaiaduarte.todoapi.domain.user.UserRole;
import com.samanthamaiaduarte.todoapi.exception.UserAlreadyExistsException;
import com.samanthamaiaduarte.todoapi.exception.UserNotFoundException;
import com.samanthamaiaduarte.todoapi.infra.security.TokenService;
import com.samanthamaiaduarte.todoapi.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private TokenService tokenService;
    @Mock
    private UserRepository repository;
    @InjectMocks
    private AuthenticationService authenticationService;

    @Test
    @DisplayName("Check if login returns a valid token when credentials are correct")
    void testLogin1() {
        //Arrange
        String username = "usertest";
        String password = "password";
        User user = new User(UUID.randomUUID(), username, password, UserRole.USER);
        LoginResponseDTO expectedToken = new LoginResponseDTO(LocalDateTime.now().plusHours(2), "bearer", "token", 7200);
        Authentication auth = mock(Authentication.class);

        var usernamePassword = new UsernamePasswordAuthenticationToken(username, password);

        when(authenticationManager.authenticate(usernamePassword)).thenReturn(auth);
        when(auth.getPrincipal()).thenReturn(user);
        when(tokenService.generateToken(user)).thenReturn(expectedToken);

        //Act
        LoginResponseDTO result = authenticationService.login(username, password);

        //Assert
        assertEquals(expectedToken, result);
        verify(authenticationManager).authenticate(usernamePassword);
        verify(auth).getPrincipal();
        verify(tokenService).generateToken(user);
    }

    @Test
    @DisplayName("Check if login throws a UserNotFoundException when credentials are incorrect")
    void testLogin2() {
        //Arrange
        String username = "usertest";
        String password = "password";

        var usernamePassword = new UsernamePasswordAuthenticationToken(username, password);

        when(authenticationManager.authenticate(usernamePassword)).thenThrow(new BadCredentialsException("Credentials are invalid."));

        //Act & Assert
        assertThrows(UserNotFoundException.class, () -> {
            authenticationService.login(username, password);
        });

        verify(authenticationManager).authenticate(usernamePassword);
    }

    @Test
    @DisplayName("Check if a new user is registered successfully")
    void testRegister1() {
        //Arrange
        RegisterDTO dto = new RegisterDTO("usertest", "password");
        UserRole role = UserRole.USER;

        when(repository.findByLogin(dto.login())).thenReturn(null);

        //Act
        authenticationService.register(dto, role);

        //Assert
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);

        verify(repository).findByLogin(dto.login());
        verify(repository).save(userCaptor.capture());

        User savedUser = userCaptor.getValue();
        assertEquals(dto.login(), savedUser.getLogin());
        assertNotEquals(dto.password(), savedUser.getPassword());
        assertTrue(new BCryptPasswordEncoder().matches(dto.password(), savedUser.getPassword()));
        assertEquals(role, savedUser.getRole());
    }

    @Test
    @DisplayName("Check if register throws a UserAlreadyExistsException when trying to register user who is already registered")
    void testRegister2() {
        //Arrange
        RegisterDTO dto = new RegisterDTO("usertest", "password");
        UserRole role = UserRole.USER;

        User existingUser = new User(UUID.randomUUID(), dto.login(), dto.password(), role);

        when(repository.findByLogin(dto.login())).thenReturn(existingUser);

        //Act & Assert
        assertThrows(UserAlreadyExistsException.class, () -> {
            authenticationService.register(dto, role);
        });

        verify(repository).findByLogin(dto.login());
        verify(repository, never()).save(any());
    }
}