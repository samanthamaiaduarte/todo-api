package com.samanthamaiaduarte.todoapi.domain.task;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

@JsonIgnoreProperties(ignoreUnknown = true)
public record TaskRequestDTO(
        @NotBlank(message = "Task title can't be empty.")
        @Size(max = 100, message = "Task title max size is 100 characters.")
        String title,

        @Size(max = 500, message = "Task title max size is 500 characters.")
        String description,

        @NotNull(message = "Task Due Date can't be empty.")
        @FutureOrPresent(message = "Task Due Date must be in the present ou future.")
        LocalDate dueDate)
{ }
