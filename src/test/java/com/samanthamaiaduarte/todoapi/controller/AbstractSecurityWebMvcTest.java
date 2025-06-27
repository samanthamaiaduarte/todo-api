package com.samanthamaiaduarte.todoapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.samanthamaiaduarte.todoapi.config.JacksonConfigTest;
import com.samanthamaiaduarte.todoapi.domain.user.UserRole;
import com.samanthamaiaduarte.todoapi.infra.security.TokenService;
import com.samanthamaiaduarte.todoapi.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import com.samanthamaiaduarte.todoapi.domain.user.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;

@SpringBootTest
@AutoConfigureMockMvc
@Import(JacksonConfigTest.class)
abstract class AbstractSecurityWebMvcTest {
    @Autowired
    protected MockMvc mockMvc;
    @Autowired
    protected ObjectMapper objectMapper;

    @MockBean
    protected TokenService tokenService;
    @MockBean
    protected UserRepository userRepository;

    protected final String ADMIN_TOKEN = "admin-token";
    protected final String USER_TOKEN = "user-token";

    protected final UserDetails adminUser = new User(UUID.randomUUID(), "useradmin", "pass", UserRole.ADMIN);
    protected final UserDetails userUser = new User(UUID.randomUUID(), "usertest", "pass", UserRole.USER);

    @BeforeEach
    void globalSetUp() {
        when(tokenService.validateToken(ADMIN_TOKEN)).thenReturn(adminUser.getUsername());
        when(userRepository.findByLogin(adminUser.getUsername())).thenReturn(adminUser);

        when(tokenService.validateToken(USER_TOKEN)).thenReturn(userUser.getUsername());
        when(userRepository.findByLogin(userUser.getUsername())).thenReturn(userUser);

        when(tokenService.validateToken(argThat(token ->
                !ADMIN_TOKEN.equals(token) && !USER_TOKEN.equals(token))))
                .thenThrow(new RuntimeException("Invalid token"));
    }

    @AfterEach
    void globalDestroyer() {
        SecurityContextHolder.clearContext();
    }

    void setSecurityContext(UserRole role) {
        if(role == UserRole.ADMIN) {
            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(adminUser, null, List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));
            SecurityContextHolder.getContext().setAuthentication(auth);
        } else if(role == UserRole.USER) {
            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(userUser, null, List.of(new SimpleGrantedAuthority("ROLE_USER")));
            SecurityContextHolder.getContext().setAuthentication(auth);
        }
    }
}
