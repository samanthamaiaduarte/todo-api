package com.samanthamaiaduarte.todoapi.domain.user;

import java.time.LocalDateTime;

public record LoginResponseDTO(LocalDateTime refresh, String token_type, String access_token, Integer expires_in) {
}
