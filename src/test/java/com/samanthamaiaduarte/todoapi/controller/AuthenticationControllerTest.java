package com.samanthamaiaduarte.todoapi.controller;

import com.samanthamaiaduarte.todoapi.domain.user.AuthenticationDTO;
import com.samanthamaiaduarte.todoapi.domain.user.LoginResponseDTO;
import com.samanthamaiaduarte.todoapi.domain.user.RegisterDTO;
import com.samanthamaiaduarte.todoapi.domain.user.UserRole;
import com.samanthamaiaduarte.todoapi.exception.UserAlreadyExistsException;
import com.samanthamaiaduarte.todoapi.exception.UserNotFoundException;
import com.samanthamaiaduarte.todoapi.service.AuthenticationService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;

import java.time.LocalDateTime;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class AuthenticationControllerTest extends AbstractSecurityWebMvcTest {
    @MockBean
    private AuthenticationService authenticationService;

    @Test
    @DisplayName("POST /auth/login should return 200 and a token when credentials are valid")
    void testLoginSuccess() throws Exception{
        LocalDateTime refresh = LocalDateTime.of(2025, 6, 19, 20, 15);
        AuthenticationDTO data = new AuthenticationDTO("usertest", "12345");
        LoginResponseDTO expectedToken = new LoginResponseDTO(refresh, "bearer", "token", 7200);

        when(authenticationService.login(data.login(), data.password())).thenReturn(expectedToken);

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(data)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token_type").value("bearer"))
                .andExpect(jsonPath("$.access_token").value("token"))
                .andExpect(jsonPath("$.expires_in").value(7200));
    }

    @Test
    @DisplayName("POST /auth/login should return 400 when login is blank")
    void testLoginFailure1() throws Exception {
        AuthenticationDTO data = new AuthenticationDTO("", "password");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(data)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorMessage.login").value("Invalid username"));
    }

    @Test
    @DisplayName("POST /auth/login should return 400 when password is blank")
    void testLoginFailure2() throws Exception {
        AuthenticationDTO data = new AuthenticationDTO("usertest", "");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(data)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorMessage.password").value("Invalid password"));
    }

    @Test
    @DisplayName("POST /auth/login should return 400 when JSON is malformed")
    void testLoginFailure4() throws Exception {
        String malformedJson = "{\"login\": \"usertest\", \"password\": ";

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(malformedJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /auth/login should return 401 when credentials are invalid")
    void testLoginFailure() throws Exception {
        AuthenticationDTO data = new AuthenticationDTO("usertest", "wrongpassword");

        when(authenticationService.login(data.login(), data.password()))
                .thenThrow(new UserNotFoundException());

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(data)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.errorMessage").value("User not found or wrong password."));
    }

    @Test
    @DisplayName("POST /auth/login should return 415 when content type is not JSON")
    void testLoginFailure3() throws Exception {
        String invalidBody = "login=usertest&password=password";

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .content(invalidBody))
                .andExpect(status().isUnsupportedMediaType());
    }

    @Test
    @DisplayName("POST /auth/register should return 201 when credentials are created")
    void testRegisterSuccess() throws Exception {
        RegisterDTO data = new RegisterDTO("usertest", "password");
        UserRole role = UserRole.USER;

        doNothing().when(authenticationService).register(data, role);

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(data)))
                .andExpect(status().isCreated());

        verify(authenticationService, times(1)).register(data, role);
    }

    @Test
    @DisplayName("POST /auth/register should return 400 when login is blank")
    void testRegisterFailure1() throws Exception {
        RegisterDTO data = new RegisterDTO("", "password");

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(data)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorMessage.login").value("Invalid username"));
    }

    @Test
    @DisplayName("POST /auth/register should return 400 when password is blank")
    void testRegisterFailure2() throws Exception {
        RegisterDTO data = new RegisterDTO("usertest", "");

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(data)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorMessage.password").value("Invalid password"));
    }

    @Test
    @DisplayName("POST /auth/login should return 400 when JSON is malformed")
    void testRegisterFailure4() throws Exception {
        String malformedJson = "{\"login\": \"usertest\", \"password\": ";

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(malformedJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /auth/register should return 409 when login already exists")
    void testRegisterFailure() throws Exception {
        RegisterDTO data = new RegisterDTO("usertest", "password");
        UserRole role = UserRole.USER;

        doThrow(new UserAlreadyExistsException()).when(authenticationService).register(data, role);

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(data)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.errorMessage").value("User already exists."));
    }

    @Test
    @DisplayName("POST /auth/login should return 415 when content type is not JSON")
    void testRegisterFailure3() throws Exception {
        String invalidBody = "login=usertest&password=password";

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .content(invalidBody))
                .andExpect(status().isUnsupportedMediaType());
    }
}