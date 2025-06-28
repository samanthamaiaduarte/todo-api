package com.samanthamaiaduarte.todoapi.domain.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(name = "User registration", description = "Payload for user creation")
public record RegisterDTO(
        @NotBlank(message = "Invalid username")
        String login,
        @NotBlank(message = "Invalid password")
        String password) {
}
