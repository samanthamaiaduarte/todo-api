package com.samanthamaiaduarte.todoapi.controller;

import com.samanthamaiaduarte.todoapi.domain.task.TaskRequestDTO;
import com.samanthamaiaduarte.todoapi.domain.task.TaskResponseDTO;
import com.samanthamaiaduarte.todoapi.domain.user.User;
import com.samanthamaiaduarte.todoapi.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "Tasks", description = "Tasks management")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/tasks")
public class TaskController {

    @Autowired
    private TaskService taskService;

    @Operation(description = "Create a new task for the user in the token.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", ref = "201task"),
            @ApiResponse(responseCode = "400", ref = "400"),
            @ApiResponse(responseCode = "401", ref = "401"),
            @ApiResponse(responseCode = "403", ref = "403"),
            @ApiResponse(responseCode = "415", ref = "415")
    })
    @PostMapping
    public ResponseEntity<TaskResponseDTO> createTask(@Valid @RequestBody TaskRequestDTO data, @AuthenticationPrincipal User user) {
        TaskResponseDTO taskResponseDTO = taskService.createTask(data, user);
        return ResponseEntity.status(HttpStatus.CREATED).body(taskResponseDTO);
    }

    @Operation(description = "Update a task from the user in the token.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", ref = "200updateTask"),
            @ApiResponse(responseCode = "400", ref = "400"),
            @ApiResponse(responseCode = "401", ref = "401"),
            @ApiResponse(responseCode = "403", ref = "403"),
            @ApiResponse(responseCode = "404", ref = "404"),
            @ApiResponse(responseCode = "415", ref = "415")
    })
    @PutMapping(value = "/{taskId}", name = "Id")
    public ResponseEntity<TaskResponseDTO> updateTask(@PathVariable @Parameter(name = "taskId", description = "Task identifier", required = true, example = "9e8088d0-c495-40cd-8fe5-6f76857c677f") UUID taskId, @Valid @RequestBody TaskRequestDTO data, @AuthenticationPrincipal User user) {
        TaskResponseDTO taskResponseDTO = taskService.updateTask(taskId, data, user);
        return ResponseEntity.status(HttpStatus.OK).body(taskResponseDTO);
    }

    @Operation(description = "Delete a task from the user in the token.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", ref = "204deleteTask"),
            @ApiResponse(responseCode = "400", ref = "400"),
            @ApiResponse(responseCode = "401", ref = "401"),
            @ApiResponse(responseCode = "403", ref = "403"),
            @ApiResponse(responseCode = "404", ref = "404")
    })
    @DeleteMapping("/{taskId}")
    public ResponseEntity<Void> deleteTask(@PathVariable @Parameter(name = "taskId", description = "Task identifier", required = true, example = "9e8088d0-c495-40cd-8fe5-6f76857c677f") UUID taskId, @AuthenticationPrincipal User user) {
        taskService.deleteTask(taskId, user);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @Operation(description = "Set a task from the user in the token as completed.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", ref = "204completeTask"),
            @ApiResponse(responseCode = "400", ref = "400"),
            @ApiResponse(responseCode = "401", ref = "401"),
            @ApiResponse(responseCode = "403", ref = "403"),
            @ApiResponse(responseCode = "404", ref = "404")
    })
    @PatchMapping("/completed/{taskId}")
    public ResponseEntity<Void> completedTask(@PathVariable @Parameter(name = "taskId", description = "Task identifier", required = true, example = "9e8088d0-c495-40cd-8fe5-6f76857c677f") UUID taskId, @AuthenticationPrincipal User user) {
        taskService.completedTask(taskId, user);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @Operation(description = "Shows a task from the user in the token.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", ref = "200"),
            @ApiResponse(responseCode = "400", ref = "400"),
            @ApiResponse(responseCode = "401", ref = "401"),
            @ApiResponse(responseCode = "403", ref = "403"),
            @ApiResponse(responseCode = "404", ref = "404")
    })
    @GetMapping("{taskId}")
    public ResponseEntity<TaskResponseDTO> selectTask(@PathVariable @Parameter(name = "taskId", description = "Task identifier", required = true, example = "9e8088d0-c495-40cd-8fe5-6f76857c677f") UUID taskId, @AuthenticationPrincipal User user) {
        TaskResponseDTO taskResponseDTO = taskService.selectTask(taskId, user);
        return ResponseEntity.status(HttpStatus.OK).body(taskResponseDTO);
    }

    @Operation(description = "Shows a list of uncompleted tasks from the user in the token.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", ref = "200uncompletedListTask"),
            @ApiResponse(responseCode = "400", ref = "400"),
            @ApiResponse(responseCode = "401", ref = "401"),
            @ApiResponse(responseCode = "403", ref = "403"),
            @ApiResponse(responseCode = "404", ref = "404")
    })
    @GetMapping
    public ResponseEntity<List<TaskResponseDTO>> selectTasks(@AuthenticationPrincipal User user) {
        List<TaskResponseDTO> taskResponseDTO = taskService.selectTasks(user, false);
        return ResponseEntity.status(HttpStatus.OK).body(taskResponseDTO);
    }

    @Operation(description = "Shows a list of completed tasks from the user in the token.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", ref = "200completedListTask"),
            @ApiResponse(responseCode = "400", ref = "400"),
            @ApiResponse(responseCode = "401", ref = "401"),
            @ApiResponse(responseCode = "403", ref = "403"),
            @ApiResponse(responseCode = "404", ref = "404")
    })
    @GetMapping("/completed")
    public ResponseEntity<List<TaskResponseDTO>> selectCompletedTasks(@AuthenticationPrincipal User user) {
        List<TaskResponseDTO> taskResponseDTO = taskService.selectTasks(user, true);
        return ResponseEntity.status(HttpStatus.OK).body(taskResponseDTO);
    }
}
