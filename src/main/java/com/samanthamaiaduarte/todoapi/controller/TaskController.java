package com.samanthamaiaduarte.todoapi.controller;

import com.samanthamaiaduarte.todoapi.domain.task.TaskRequestDTO;
import com.samanthamaiaduarte.todoapi.domain.task.TaskResponseDTO;
import com.samanthamaiaduarte.todoapi.domain.user.User;
import com.samanthamaiaduarte.todoapi.service.TaskService;
import jakarta.validation.Valid;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    @Autowired
    private TaskService taskService;

    @PostMapping
    public ResponseEntity<TaskResponseDTO> createTask(@Valid @RequestBody TaskRequestDTO data, @AuthenticationPrincipal User user) {
        TaskResponseDTO taskResponseDTO = taskService.createTask(data, user);
        return ResponseEntity.status(HttpStatus.CREATED).body(taskResponseDTO);
    }

    @PutMapping("/{taskId}")
    public ResponseEntity<TaskResponseDTO> updateTask(@PathVariable UUID taskId, @Valid @RequestBody TaskRequestDTO data, @AuthenticationPrincipal User user) {
        TaskResponseDTO taskResponseDTO = taskService.updateTask(taskId, data, user);
        return ResponseEntity.status(HttpStatus.OK).body(taskResponseDTO);
    }

    @DeleteMapping("/{taskId}")
    public ResponseEntity<Void> deleteTask(@PathVariable UUID taskId, @AuthenticationPrincipal User user) {
        taskService.deleteTask(taskId, user);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PatchMapping("/completed/{taskId}")
    public ResponseEntity<Void> completedTask(@PathVariable UUID taskId, @AuthenticationPrincipal User user) {
        taskService.completedTask(taskId, user);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("{taskId}")
    public ResponseEntity<TaskResponseDTO> selectTask(@PathVariable UUID taskId, @AuthenticationPrincipal User user) {
        TaskResponseDTO taskResponseDTO = taskService.selectTask(taskId, user);
        return ResponseEntity.status(HttpStatus.OK).body(taskResponseDTO);
    }

    @GetMapping
    public ResponseEntity<List<TaskResponseDTO>> selectTasks(@AuthenticationPrincipal User user) {
        List<TaskResponseDTO> taskResponseDTO = taskService.selectTasks(user, false);
        return ResponseEntity.status(HttpStatus.OK).body(taskResponseDTO);
    }

    @GetMapping("/completed")
    public ResponseEntity<List<TaskResponseDTO>> selectCompletedTasks(@AuthenticationPrincipal User user) {
        List<TaskResponseDTO> taskResponseDTO = taskService.selectTasks(user, true);
        return ResponseEntity.status(HttpStatus.OK).body(taskResponseDTO);
    }
}
