package com.samanthamaiaduarte.todoapi.service;

import com.samanthamaiaduarte.todoapi.domain.user.User;
import com.samanthamaiaduarte.todoapi.domain.user.UserRole;
import com.samanthamaiaduarte.todoapi.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthorizationServiceTest {
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private AuthorizationService authorizationService;

    @Test
    @DisplayName("Check if a user can be found by his login and return a UserDetail")
    void loadUserByUsername1() {
        //Arrange
        String username = "usertest";
        User user = new User(UUID.randomUUID(), "usertest", "test", UserRole.USER);

        when(userRepository.findByLogin(username)).thenReturn(user);

        //Act
        UserDetails result = authorizationService.loadUserByUsername(username);

        //Assert
        assertEquals(user, result);
        verify(userRepository).findByLogin(username);
    }

    @Test
    @DisplayName("Check if a user can't be found by his login and throws a UsernameNotFoundException")
    void loadUserByUsername2() {
        //Arrange
        String username = "usertest";

        when(userRepository.findByLogin(username)).thenReturn(null);

        //Act & Assert
        assertThrows(UsernameNotFoundException.class, () -> {
            authorizationService.loadUserByUsername(username);
        });

        verify(userRepository).findByLogin(username);
    }
}