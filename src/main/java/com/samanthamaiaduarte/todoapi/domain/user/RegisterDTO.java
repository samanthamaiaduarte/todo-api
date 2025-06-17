package com.samanthamaiaduarte.todoapi.domain.user;

public record RegisterDTO(String login, String password, UserRole role) {
}
