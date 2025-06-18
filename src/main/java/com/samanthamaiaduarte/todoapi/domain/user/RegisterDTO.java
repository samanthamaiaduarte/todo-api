package com.samanthamaiaduarte.todoapi.domain.user;

import jakarta.validation.constraints.NotBlank;

public record RegisterDTO(
        @NotBlank(message = "Invalid username")
        String login,
        @NotBlank(message = "Invalid password")
        String password) {
}
