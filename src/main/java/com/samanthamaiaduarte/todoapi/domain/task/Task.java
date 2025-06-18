package com.samanthamaiaduarte.todoapi.domain.task;

import com.samanthamaiaduarte.todoapi.domain.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "tasks")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Task {
    @Id
    @GeneratedValue
    private UUID id;

    private String title;
    private String description;
    private LocalDate dueDate;
    private Boolean completed;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
