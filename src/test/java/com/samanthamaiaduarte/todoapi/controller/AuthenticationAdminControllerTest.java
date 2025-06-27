package com.samanthamaiaduarte.todoapi.controller;

import com.samanthamaiaduarte.todoapi.domain.user.RegisterDTO;
import com.samanthamaiaduarte.todoapi.domain.user.UserRole;
import com.samanthamaiaduarte.todoapi.exception.ApiTokenExpiredException;
import com.samanthamaiaduarte.todoapi.exception.ApiTokenInvalidException;
import com.samanthamaiaduarte.todoapi.exception.UserAlreadyExistsException;
import com.samanthamaiaduarte.todoapi.infra.security.SecurityConfiguration;
import com.samanthamaiaduarte.todoapi.service.AuthenticationService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;

import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class AuthenticationAdminControllerTest extends AbstractSecurityWebMvcTest {
    @MockBean
    private AuthenticationService authenticationService;

    @Test
    @DisplayName("POST /admin/register should return 201 when credentials are created")
    void testAdminRegisterSuccess() throws Exception {
        setSecurityContext(UserRole.ADMIN);

        RegisterDTO data = new RegisterDTO("admin", "password");

        mockMvc.perform(post("/admin/register")
                        .header("Authorization", "Bearer admin-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(data)))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("POST /admin/login should return 400 when JSON is malformed")
    void testAdminRegisterFailure4() throws Exception {
        setSecurityContext(UserRole.ADMIN);

        String malformedJson = "{\"login\": \"admin\", \"password\": "; // JSON quebrado

        mockMvc.perform(post("/admin/register")
                        .header("Authorization", "Bearer admin-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(malformedJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /admin/register should return 400 when login is blank")
    void testAdminRegisterFailure1() throws Exception {
        setSecurityContext(UserRole.ADMIN);

        RegisterDTO data = new RegisterDTO("", "password");

        mockMvc.perform(post("/admin/register")
                        .header("Authorization", "Bearer admin-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(data)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorMessage.login").value("Invalid username"));
    }

    @Test
    @DisplayName("POST /admin/register should return 400 when password is blank")
    void testAdminRegisterFailure2() throws Exception {
        setSecurityContext(UserRole.ADMIN);

        RegisterDTO data = new RegisterDTO("admin", "");

        mockMvc.perform(post("/admin/register")
                        .header("Authorization", "Bearer admin-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(data)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorMessage.password").value("Invalid password"));
    }

    @Test
    @DisplayName("POST /admin/register should return 401 when a invalid token is provided")
    void testTaskSelectListUnauthorized1() throws Exception {
        RegisterDTO data = new RegisterDTO("admin", "password");

        doThrow(new ApiTokenInvalidException("Invalid token.")).when(tokenService).validateToken("invalid-token");

        mockMvc.perform(post("/admin/register")
                        .header("Authorization", "Bearer invalid-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(data)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("POST /admin/register should return 401 when a expired token is provided")
    void testTaskSelectListUnauthorized2() throws Exception {
        RegisterDTO data = new RegisterDTO("admin", "password");

        doThrow(new ApiTokenExpiredException("Token has expired.")).when(tokenService).validateToken("expired-token");

        mockMvc.perform(post("/admin/register")
                        .header("Authorization", "Bearer expired-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(data)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("POST /admin/register should return 403 when no token is provided")
    void testAdminRegisterForbidden1() throws Exception {
        RegisterDTO data = new RegisterDTO("admin", "password");

        mockMvc.perform(post("/admin/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(data)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("POST /admin/register should return 403 when user does not have ADMIN role")
    void testAdminRegisterForbidden2() throws Exception {
        setSecurityContext(UserRole.ADMIN);

        RegisterDTO data = new RegisterDTO("admin", "password");

        mockMvc.perform(post("/admin/register")
                        .header("Authorization", "Bearer user-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(data)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("POST /admin/register should return 409 when login already exists")
    void testAdminRegisterFailure() throws Exception {
        setSecurityContext(UserRole.ADMIN);

        RegisterDTO data = new RegisterDTO("admin", "password");
        UserRole role = UserRole.ADMIN;

        doThrow(new UserAlreadyExistsException()).when(authenticationService).register(data, role);

        mockMvc.perform(post("/admin/register")
                        .header("Authorization", "Bearer admin-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(data)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.errorMessage").value("User already exists."));
    }

    @Test
    @DisplayName("POST /admin/login should return 415 when content type is not JSON")
    void testAdminRegisterFailure3() throws Exception {
        setSecurityContext(UserRole.ADMIN);

        String invalidBody = "login=admin&password=password";

        mockMvc.perform(post("/admin/register")
                        .header("Authorization", "Bearer admin-token")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .content(invalidBody))
                .andExpect(status().isUnsupportedMediaType());
    }


}