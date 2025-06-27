package com.samanthamaiaduarte.todoapi.controller;

import com.samanthamaiaduarte.todoapi.domain.task.TaskRequestDTO;
import com.samanthamaiaduarte.todoapi.domain.task.TaskResponseDTO;
import com.samanthamaiaduarte.todoapi.domain.user.User;
import com.samanthamaiaduarte.todoapi.domain.user.UserRole;
import com.samanthamaiaduarte.todoapi.exception.ApiTokenExpiredException;
import com.samanthamaiaduarte.todoapi.exception.ApiTokenInvalidException;
import com.samanthamaiaduarte.todoapi.exception.TaskForbiddenException;
import com.samanthamaiaduarte.todoapi.exception.TaskNotFoundException;
import com.samanthamaiaduarte.todoapi.service.TaskService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class TaskControllerTest extends AbstractSecurityWebMvcTest {
    @MockBean
    private TaskService taskService;

    @Test
    @DisplayName("POST /tasks should return 201 when a task was created")
    void testTaskCreateSuccess() throws Exception {
        setSecurityContext(UserRole.ADMIN);

        LocalDate created = LocalDate.now();
        TaskRequestDTO data = new TaskRequestDTO("Test 1", "Task test 1", created);
        TaskResponseDTO taskResponseDTO = new TaskResponseDTO(UUID.randomUUID(), "Test 1", "Task test 1", created, false);

        when(taskService.createTask(eq(data), any(User.class))).thenReturn(taskResponseDTO);

        mockMvc.perform(post("/tasks")
                        .header("Authorization", "Bearer admin-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(data)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Test 1"))
                .andExpect(jsonPath("$.description").value("Task test 1"))
                .andExpect(jsonPath("$.completed").value(false));

        verify(taskService).createTask(eq(data), any(User.class));
    }

    @Test
    @DisplayName("POST /tasks should return 400 when a task has invalid arguments")
    void testTaskCreateFailed1() throws Exception {
        setSecurityContext(UserRole.ADMIN);

        LocalDate created = LocalDate.now().minusDays(1);
        TaskRequestDTO data = new TaskRequestDTO("", "Task test 1", created);

        mockMvc.perform(post("/tasks")
                        .header("Authorization", "Bearer admin-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(data)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /tasks should return 400 when a required field is missing")
    void testTaskCreateFailed2() throws Exception {
        setSecurityContext(UserRole.ADMIN);

        String data = """
        {
          "title": "Task title",
          "description": "Desc"
        }
        """;

        mockMvc.perform(post("/tasks")
                        .header("Authorization", "Bearer admin-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(data)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /tasks should return 400 when description exceeds 500 characters")
    void testTaskCreateFailed3() throws Exception {
        setSecurityContext(UserRole.ADMIN);

        String longDescription = "a".repeat(501);
        TaskRequestDTO data = new TaskRequestDTO("Task", longDescription, LocalDate.now());

        mockMvc.perform(post("/tasks")
                        .header("Authorization", "Bearer admin-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(data)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /tasks should return 400 when title exceeds 100 characters")
    void testTaskCreateFailed4() throws Exception {
        setSecurityContext(UserRole.ADMIN);

        String longTitle = "a".repeat(101);
        TaskRequestDTO data = new TaskRequestDTO(longTitle, "desc", LocalDate.now());

        mockMvc.perform(post("/tasks")
                        .header("Authorization", "Bearer admin-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(data)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /tasks should return 401 when a invalid token is provided")
    void testTaskCreateUnauthorized1() throws Exception {
        LocalDate created = LocalDate.now();
        TaskRequestDTO data = new TaskRequestDTO("Task 1", "Task test 1", created);

        doThrow(new ApiTokenInvalidException("Invalid token.")).when(tokenService).validateToken("invalid-token");

        mockMvc.perform(post("/tasks")
                        .header("Authorization", "Bearer invalid-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(data)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("POST /tasks should return 401 when a expired token is provided")
    void testTaskCreateUnauthorized2() throws Exception {
        LocalDate created = LocalDate.now();
        TaskRequestDTO data = new TaskRequestDTO("Task 1", "Task test 1", created);

        doThrow(new ApiTokenExpiredException("Token has expired.")).when(tokenService).validateToken("expired-token");

        mockMvc.perform(post("/tasks")
                        .header("Authorization", "Bearer expired-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(data)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("POST /tasks should return 403 when no token is provided")
    void testTaskCreateForbidden1 () throws Exception {
        LocalDate created = LocalDate.now();
        TaskRequestDTO data = new TaskRequestDTO("Test 1", "Task test 1", created);

        mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(data)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("POST /tasks should return 415 when content type is not JSON")
    void testTaskCreateUnsupported() throws Exception {
        setSecurityContext(UserRole.ADMIN);

        LocalDate created = LocalDate.now();
        String invalidBody = "title=Task%201&description=Task%20test%201&dueDate=" + created.toString();

        mockMvc.perform(post("/tasks")
                        .header("Authorization", "Bearer admin-token")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .content(invalidBody))
                .andExpect(status().isUnsupportedMediaType());
    }

    @Test
    @DisplayName("PUT /tasks/{taskId} should return 200 when task is successfully updated")
    void testUpdateTaskSuccess() throws Exception {
        setSecurityContext(UserRole.ADMIN);

        UUID taskId = UUID.randomUUID();
        LocalDate created = LocalDate.now();
        TaskRequestDTO data = new TaskRequestDTO("Updated Title", "Updated Description", created);
        TaskResponseDTO response = new TaskResponseDTO(taskId, "Updated Title", "Updated Description", created, false);

        when(taskService.updateTask(eq(taskId), eq(data), any(User.class))).thenReturn(response);

        mockMvc.perform(put("/tasks/{taskId}", taskId)
                        .header("Authorization", "Bearer admin-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(data)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(taskId.toString()))
                .andExpect(jsonPath("$.title").value("Updated Title"))
                .andExpect(jsonPath("$.description").value("Updated Description"))
                .andExpect(jsonPath("$.completed").value(false));

        verify(taskService).updateTask(eq(taskId), eq(data), any(User.class));
    }

    @Test
    @DisplayName("PUT /tasks/{taskId} should return 400 when a task has invalid arguments")
    void testTaskUpdateFailed1() throws Exception {
        setSecurityContext(UserRole.ADMIN);

        UUID taskId = UUID.randomUUID();
        LocalDate created = LocalDate.now().minusDays(1);
        TaskRequestDTO data = new TaskRequestDTO("", "Task test 1", created);

        mockMvc.perform(put("/tasks/{taskId}", taskId)
                        .header("Authorization", "Bearer admin-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(data)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PUT /tasks/{taskId} should return 400 when a required field is missing")
    void testTaskUpdateFailed2() throws Exception {
        setSecurityContext(UserRole.ADMIN);

        UUID taskId = UUID.randomUUID();
        String data = """
        {
          "title": "Task title",
          "description": "Desc"
        }
        """;

        mockMvc.perform(put("/tasks/{taskId}", taskId)
                        .header("Authorization", "Bearer admin-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(data)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PUT /tasks/{taskId} should return 400 when description exceeds 500 characters")
    void testTaskUpdateFailed3() throws Exception {
        setSecurityContext(UserRole.ADMIN);

        UUID taskId = UUID.randomUUID();
        String longDescription = "a".repeat(501);
        TaskRequestDTO data = new TaskRequestDTO("Task", longDescription, LocalDate.now());

        mockMvc.perform(put("/tasks/{taskId}", taskId)
                        .header("Authorization", "Bearer admin-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(data)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PUT /tasks/{taskId} should return 400 when title exceeds 100 characters")
    void testTaskUpdateFailed4() throws Exception {
        setSecurityContext(UserRole.ADMIN);

        UUID taskId = UUID.randomUUID();
        String longTitle = "a".repeat(101);
        TaskRequestDTO data = new TaskRequestDTO(longTitle, "desc", LocalDate.now());

        mockMvc.perform(put("/tasks/{taskId}", taskId)
                        .header("Authorization", "Bearer admin-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(data)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PUT /tasks/{taskId} should return 400 when taskId is not a valid UUID")
    void testTaskUpdateFailed5() throws Exception {
        setSecurityContext(UserRole.ADMIN);

        String invalidTaskId = "not-a-uuid";
        TaskRequestDTO data = new TaskRequestDTO("Test", "Test desc", LocalDate.now());

        mockMvc.perform(put("/tasks/{taskId}", invalidTaskId)
                        .header("Authorization", "Bearer admin-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(data)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PUT /tasks/{taskId} should return 401 when a invalid token is provided")
    void testTaskUpdateUnauthorized1() throws Exception {
        UUID taskId = UUID.randomUUID();
        LocalDate created = LocalDate.now();
        TaskRequestDTO data = new TaskRequestDTO("Task 1", "Task test 1", created);

        doThrow(new ApiTokenInvalidException("Invalid token.")).when(tokenService).validateToken("invalid-token");

        mockMvc.perform(put("/tasks/{taskId}", taskId)
                        .header("Authorization", "Bearer invalid-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(data)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("PUT /tasks/{taskId} should return 401 when a expired token is provided")
    void testTaskUpdateUnauthorized2() throws Exception {
        UUID taskId = UUID.randomUUID();
        LocalDate created = LocalDate.now();
        TaskRequestDTO data = new TaskRequestDTO("Task 1", "Task test 1", created);

        doThrow(new ApiTokenExpiredException("Token has expired.")).when(tokenService).validateToken("expired-token");

        mockMvc.perform(put("/tasks/{taskId}", taskId)
                        .header("Authorization", "Bearer expired-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(data)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("PUT /tasks/{taskId} should return 403 when no token is provided")
    void testTaskUpdateForbidden1 () throws Exception {
        UUID taskId = UUID.randomUUID();
        LocalDate created = LocalDate.now();
        TaskRequestDTO data = new TaskRequestDTO("Test 1", "Task test 1", created);

        mockMvc.perform(put("/tasks/{taskId}", taskId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(data)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("PUT /tasks/{taskId} should return 403 when task does not belong to the user")
    void testTaskUpdateForbidden2 () throws Exception {
        setSecurityContext(UserRole.ADMIN);

        UUID taskId = UUID.randomUUID();
        LocalDate created = LocalDate.now();
        TaskRequestDTO data = new TaskRequestDTO("Test 1", "Task test 1", created);

        doThrow(new TaskForbiddenException()).when(taskService).updateTask(eq(taskId), eq(data), any(User.class));

        mockMvc.perform(put("/tasks/{taskId}", taskId)
                        .header("Authorization", "Bearer user-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(data)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("PUT /tasks/{taskId} should return 404 when task does not exists")
    void testTaskUpdateNotFound1 () throws Exception {
        setSecurityContext(UserRole.ADMIN);

        UUID taskId = UUID.randomUUID();
        LocalDate created = LocalDate.now();
        TaskRequestDTO data = new TaskRequestDTO("Test 1", "Task test 1", created);

        doThrow(new TaskNotFoundException()).when(taskService).updateTask(eq(taskId), eq(data), any(User.class));

        mockMvc.perform(put("/tasks/{taskId}", taskId)
                        .header("Authorization", "Bearer admin-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(data)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("PUT /tasks/{taskId} should return 415 when content type is not JSON")
    void testTaskUpdateUnsupported() throws Exception {
        setSecurityContext(UserRole.ADMIN);

        UUID taskId = UUID.randomUUID();
        LocalDate created = LocalDate.now();
        String invalidBody = "title=Task%201&description=Task%20test%201&dueDate=" + created.toString();

        mockMvc.perform(put("/tasks/{taskId}", taskId)
                        .header("Authorization", "Bearer admin-token")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .content(invalidBody))
                .andExpect(status().isUnsupportedMediaType());
    }

    @Test
    @DisplayName("DELETE /tasks/{taskId} should return 204 when task is successfully deleted")
    void testDeleteTaskSuccess() throws Exception {
        setSecurityContext(UserRole.ADMIN);

        UUID taskId = UUID.randomUUID();

        mockMvc.perform(delete("/tasks/{taskId}", taskId)
                        .header("Authorization", "Bearer admin-token"))
                .andExpect(status().isNoContent());

        verify(taskService).deleteTask(eq(taskId), any(User.class));
    }

    @Test
    @DisplayName("DELETE /tasks/{taskId} should return 400 when taskId is not a valid UUID")
    void testTaskDeleteFailed() throws Exception {
        setSecurityContext(UserRole.ADMIN);

        String invalidTaskId = "not-a-uuid";

        mockMvc.perform(delete("/tasks/{taskId}", invalidTaskId)
                        .header("Authorization", "Bearer admin-token"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("DELETE /tasks/{taskId} should return 401 when a invalid token is provided")
    void testTaskDeleteUnauthorized1() throws Exception {
        UUID taskId = UUID.randomUUID();

        doThrow(new ApiTokenInvalidException("Invalid token.")).when(tokenService).validateToken("invalid-token");

        mockMvc.perform(delete("/tasks/{taskId}", taskId)
                        .header("Authorization", "Bearer invalid-token"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("DELETE /tasks/{taskId} should return 401 when a expired token is provided")
    void testTaskDeleteUnauthorized2() throws Exception {
        UUID taskId = UUID.randomUUID();

        doThrow(new ApiTokenExpiredException("Token has expired.")).when(tokenService).validateToken("expired-token");

        mockMvc.perform(delete("/tasks/{taskId}", taskId)
                        .header("Authorization", "Bearer expired-token"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("DELETE /tasks/{taskId} should return 403 when no token is provided")
    void testTaskDeleteForbidden1 () throws Exception {
        UUID taskId = UUID.randomUUID();

        mockMvc.perform(delete("/tasks/{taskId}", taskId))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("DELETE /tasks/{taskId} should return 403 when task does not belong to the user")
    void testTaskDeleteForbidden2 () throws Exception {
        setSecurityContext(UserRole.ADMIN);

        UUID taskId = UUID.randomUUID();

        doThrow(new TaskForbiddenException()).when(taskService).deleteTask(eq(taskId), any(User.class));

        mockMvc.perform(delete("/tasks/{taskId}", taskId)
                        .header("Authorization", "Bearer user-token"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("DELETE /tasks/{taskId} should return 404 when task does not exists")
    void testTaskDeleteNotFound1 () throws Exception {
        setSecurityContext(UserRole.ADMIN);

        UUID taskId = UUID.randomUUID();

        doThrow(new TaskNotFoundException()).when(taskService).deleteTask(eq(taskId), any(User.class));

        mockMvc.perform(delete("/tasks/{taskId}", taskId)
                        .header("Authorization", "Bearer admin-token"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("PATCH /tasks/completed/{taskId} should return 204 when task is successfully updated")
    void testCompletedTaskSuccess() throws Exception {
        setSecurityContext(UserRole.ADMIN);

        UUID taskId = UUID.randomUUID();

        mockMvc.perform(patch("/tasks/completed/{taskId}", taskId)
                        .header("Authorization", "Bearer admin-token"))
                .andExpect(status().isNoContent());

        verify(taskService).completedTask(eq(taskId), any(User.class));
    }

    @Test
    @DisplayName("PATCH /tasks/completed/{taskId} should return 400 when taskId is not a valid UUID")
    void testTaskCompleteFailed() throws Exception {
        setSecurityContext(UserRole.ADMIN);

        String invalidTaskId = "not-a-uuid";

        mockMvc.perform(patch("/tasks/completed/{taskId}", invalidTaskId)
                        .header("Authorization", "Bearer admin-token"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PATCH /tasks/completed/{taskId} should return 401 when a invalid token is provided")
    void testTaskCompleteUnauthorized1() throws Exception {
        UUID taskId = UUID.randomUUID();

        doThrow(new ApiTokenInvalidException("Invalid token.")).when(tokenService).validateToken("invalid-token");

        mockMvc.perform(patch("/tasks/completed/{taskId}", taskId)
                        .header("Authorization", "Bearer invalid-token"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("PATCH /tasks/completed/{taskId} should return 401 when a expired token is provided")
    void testTaskCompleteUnauthorized2() throws Exception {
        UUID taskId = UUID.randomUUID();

        doThrow(new ApiTokenExpiredException("Token has expired.")).when(tokenService).validateToken("expired-token");

        mockMvc.perform(patch("/tasks/completed/{taskId}", taskId)
                        .header("Authorization", "Bearer expired-token"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("PATCH /tasks/completed/{taskId} should return 403 when no token is provided")
    void testTaskCompleteForbidden1 () throws Exception {
        UUID taskId = UUID.randomUUID();

        mockMvc.perform(patch("/tasks/completed/{taskId}", taskId))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("PATCH /tasks/completed/{taskId} should return 403 when task does not belong to the user")
    void testTaskCompleteForbidden2 () throws Exception {
        setSecurityContext(UserRole.ADMIN);

        UUID taskId = UUID.randomUUID();

        doThrow(new TaskForbiddenException()).when(taskService).completedTask(eq(taskId), any(User.class));

        mockMvc.perform(patch("/tasks/completed/{taskId}", taskId)
                        .header("Authorization", "Bearer user-token"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("PATCH /tasks/completed/{taskId} should return 404 when task does not exists")
    void testTaskCompleteNotFound1 () throws Exception {
        setSecurityContext(UserRole.ADMIN);

        UUID taskId = UUID.randomUUID();

        doThrow(new TaskNotFoundException()).when(taskService).completedTask(eq(taskId), any(User.class));

        mockMvc.perform(patch("/tasks/completed/{taskId}", taskId)
                        .header("Authorization", "Bearer admin-token"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /tasks/{taskId} should return 200 when a task was founded")
    void testTaskSelectSuccess() throws Exception {
        setSecurityContext(UserRole.ADMIN);

        UUID taskId = UUID.randomUUID();
        LocalDate created = LocalDate.now();
        TaskResponseDTO taskResponseDTO = new TaskResponseDTO(UUID.randomUUID(), "Test 1", "Task test 1", created, false);

        when(taskService.selectTask(eq(taskId), any(User.class))).thenReturn(taskResponseDTO);

        mockMvc.perform(get("/tasks/{taskId}", taskId)
                        .header("Authorization", "Bearer admin-token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Test 1"))
                .andExpect(jsonPath("$.description").value("Task test 1"))
                .andExpect(jsonPath("$.completed").value(false));

        verify(taskService).selectTask(eq(taskId), any(User.class));
    }

    @Test
    @DisplayName("GET /tasks/{taskId} should return 400 when taskId is not a valid UUID")
    void testTaskSelectFailed() throws Exception {
        setSecurityContext(UserRole.ADMIN);

        String invalidTaskId = "not-a-uuid";

        mockMvc.perform(get("/tasks/{taskId}", invalidTaskId)
                        .header("Authorization", "Bearer admin-token"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /tasks/{taskId} should return 401 when a invalid token is provided")
    void testTaskSelectUnauthorized1() throws Exception {
        UUID taskId = UUID.randomUUID();

        doThrow(new ApiTokenInvalidException("Invalid token.")).when(tokenService).validateToken("invalid-token");

        mockMvc.perform(get("/tasks/{taskId}", taskId)
                        .header("Authorization", "Bearer invalid-token"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("GET /tasks/{taskId} should return 401 when a expired token is provided")
    void testTaskSelectUnauthorized2() throws Exception {
        UUID taskId = UUID.randomUUID();

        doThrow(new ApiTokenExpiredException("Token has expired.")).when(tokenService).validateToken("expired-token");

        mockMvc.perform(get("/tasks/{taskId}", taskId)
                        .header("Authorization", "Bearer expired-token"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("GET /tasks/{taskId} should return 403 when no token is provided")
    void testTaskSelectForbidden1 () throws Exception {
        UUID taskId = UUID.randomUUID();

        mockMvc.perform(get("/tasks/{taskId}", taskId))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("GET /tasks/{taskId} should return 403 when task does not belong to the user")
    void testTaskSelectForbidden2 () throws Exception {
        setSecurityContext(UserRole.ADMIN);

        UUID taskId = UUID.randomUUID();

        doThrow(new TaskForbiddenException()).when(taskService).selectTask(eq(taskId), any(User.class));

        mockMvc.perform(get("/tasks/{taskId}", taskId)
                        .header("Authorization", "Bearer user-token"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("GET /tasks/{taskId} should return 404 when task does not exists")
    void testTaskSelectNotFound1 () throws Exception {
        setSecurityContext(UserRole.ADMIN);

        UUID taskId = UUID.randomUUID();

        doThrow(new TaskNotFoundException()).when(taskService).selectTask(eq(taskId), any(User.class));

        mockMvc.perform(get("/tasks/{taskId}", taskId)
                        .header("Authorization", "Bearer admin-token"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /tasks should return 200 when a list of task was founded")
    void testTaskSelectListSuccess() throws Exception {
        setSecurityContext(UserRole.ADMIN);

        Boolean completed = false;
        LocalDate created = LocalDate.now();
        List<TaskResponseDTO> listDTO = List.of(
                new TaskResponseDTO(UUID.randomUUID(), "Test 1", "Task test 1", created, completed),
                new TaskResponseDTO(UUID.randomUUID(), "Test 2", "Task test 2", created.plusDays(1), completed),
                new TaskResponseDTO(UUID.randomUUID(), "Test 3", "Task test 3", created.minusDays(2), completed)
        );

        when(taskService.selectTasks(any(User.class), eq(completed))).thenReturn(listDTO);

        mockMvc.perform(get("/tasks")
                        .header("Authorization", "Bearer admin-token"))
                .andExpect(status().isOk());

        verify(taskService).selectTasks(any(User.class), eq(completed));
    }

    @Test
    @DisplayName("GET /tasks should return 401 when a invalid token is provided")
    void testTaskSelectListUnauthorized1() throws Exception {
        UUID taskId = UUID.randomUUID();

        doThrow(new ApiTokenInvalidException("Invalid token.")).when(tokenService).validateToken("invalid-token");

        mockMvc.perform(get("/tasks")
                        .header("Authorization", "Bearer invalid-token"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("GET /tasks should return 401 when a expired token is provided")
    void testTaskSelectListUnauthorized2() throws Exception {
        UUID taskId = UUID.randomUUID();

        doThrow(new ApiTokenExpiredException("Token has expired.")).when(tokenService).validateToken("expired-token");

        mockMvc.perform(get("/tasks")
                        .header("Authorization", "Bearer expired-token"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("GET /tasks should return 403 when no token is provided")
    void testTaskSelectListForbidden1 () throws Exception {
        UUID taskId = UUID.randomUUID();

        mockMvc.perform(get("/tasks"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("GET /tasks/completed should return 200 when a list of task was founded")
    void testTaskCompleteSelectListSuccess() throws Exception {
        setSecurityContext(UserRole.ADMIN);

        Boolean completed = true;
        LocalDate created = LocalDate.now();
        List<TaskResponseDTO> listDTO = List.of(
                new TaskResponseDTO(UUID.randomUUID(), "Test 1", "Task test 1", created, completed),
                new TaskResponseDTO(UUID.randomUUID(), "Test 2", "Task test 2", created.plusDays(1), completed),
                new TaskResponseDTO(UUID.randomUUID(), "Test 3", "Task test 3", created.minusDays(2), completed)
        );

        when(taskService.selectTasks(any(User.class), eq(completed))).thenReturn(listDTO);

        mockMvc.perform(get("/tasks/completed")
                        .header("Authorization", "Bearer admin-token"))
                .andExpect(status().isOk());

        verify(taskService).selectTasks(any(User.class), eq(completed));
    }

    @Test
    @DisplayName("GET /tasks/completed should return 401 when a invalid token is provided")
    void testTaskCompleteSelectListUnauthorized1() throws Exception {
        UUID taskId = UUID.randomUUID();

        doThrow(new ApiTokenInvalidException("Invalid token.")).when(tokenService).validateToken("invalid-token");

        mockMvc.perform(get("/tasks/completed")
                        .header("Authorization", "Bearer invalid-token"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("GET /tasks/completed should return 401 when a expired token is provided")
    void testTaskCompleteSelectListUnauthorized2() throws Exception {
        UUID taskId = UUID.randomUUID();

        doThrow(new ApiTokenExpiredException("Token has expired.")).when(tokenService).validateToken("expired-token");

        mockMvc.perform(get("/tasks/completed")
                        .header("Authorization", "Bearer expired-token"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("GET /tasks/completed should return 403 when no token is provided")
    void testTaskCompleteSelectListForbidden1 () throws Exception {
        UUID taskId = UUID.randomUUID();

        mockMvc.perform(get("/tasks/completed"))
                .andExpect(status().isForbidden());
    }
}