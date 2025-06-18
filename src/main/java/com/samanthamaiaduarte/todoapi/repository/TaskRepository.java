package com.samanthamaiaduarte.todoapi.repository;

import com.samanthamaiaduarte.todoapi.domain.task.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TaskRepository extends JpaRepository<Task, UUID> {

    List<Task> findAllByUserIdAndCompleted(UUID userId, Boolean completed);

    Task findByIdAndUserId(UUID id, UUID userId);
}
