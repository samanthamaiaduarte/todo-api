package com.samanthamaiaduarte.todoapi.domain.task;

import java.time.LocalDate;
import java.util.UUID;

public record TaskResponseDTO(UUID id, String title, String description, LocalDate dueDate, Boolean completed) {
}
