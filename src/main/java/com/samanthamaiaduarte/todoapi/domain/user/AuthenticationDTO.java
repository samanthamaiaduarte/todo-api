package com.samanthamaiaduarte.todoapi.domain.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(name = "Login", description = "Payload for login")
public record AuthenticationDTO(
        @NotBlank(message = "Invalid username")
        String login,
        @NotBlank(message = "Invalid password")
        String password) {
}
