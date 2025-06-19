package com.samanthamaiaduarte.todoapi.service;

import com.samanthamaiaduarte.todoapi.domain.task.Task;
import com.samanthamaiaduarte.todoapi.domain.task.TaskRequestDTO;
import com.samanthamaiaduarte.todoapi.domain.task.TaskResponseDTO;
import com.samanthamaiaduarte.todoapi.domain.user.User;
import com.samanthamaiaduarte.todoapi.domain.user.UserRole;
import com.samanthamaiaduarte.todoapi.exception.TaskNotFoundException;
import com.samanthamaiaduarte.todoapi.mapper.TaskMapper;
import com.samanthamaiaduarte.todoapi.repository.TaskRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {
    @Mock
    private TaskRepository taskRepository;
    @Mock
    private TaskMapper taskMapper;
    @InjectMocks
    private TaskService taskService;

    @Test
    @DisplayName("Check if a task is created and return a TaskResponseDTO")
    void testCreateTask() {
        //Arrange
        User user = new User(UUID.randomUUID(), "usertest", "test", UserRole.USER);
        TaskRequestDTO dto = new TaskRequestDTO("Test task", "Task test for create task into TaskService", LocalDate.now().plusDays(5));
        Task task = new Task(UUID.randomUUID(), dto.title(), dto.description(), dto.dueDate(), false, user);
        TaskResponseDTO expectedDto = new TaskResponseDTO(task.getId(), task.getTitle(), task.getDescription(), task.getDueDate(), task.getCompleted());

        when(taskMapper.toEntity(dto, user)).thenReturn(task);
        when(taskRepository.save(task)).thenReturn(task);
        when(taskMapper.toDto(task)).thenReturn(expectedDto);

        //Act
        TaskResponseDTO result = taskService.createTask(dto, user);

        //Assert
        assertEquals(expectedDto, result);
        verify(taskRepository).save(task);
        verify(taskMapper).toEntity(dto, user);
        verify(taskMapper).toDto(task);
    }

    @Test
    @DisplayName("Check if a task is updated and return a TaskResponseDTO")
    void testUpdateTask1() {
        //Arrange
        UUID taskId = UUID.randomUUID();
        User user = new User(UUID.randomUUID(), "usertest", "test", UserRole.USER);
        TaskRequestDTO dto = new TaskRequestDTO("Test task", "Task test for create task into TaskService", LocalDate.now().plusDays(5));
        Task task = new Task(taskId, dto.title(), dto.description(), dto.dueDate(), false, user);
        TaskResponseDTO expectedDto = new TaskResponseDTO(task.getId(), task.getTitle(), task.getDescription(), task.getDueDate(), task.getCompleted());

        when(taskRepository.findByIdAndUserId(taskId, user.getId())).thenReturn(task);
        doNothing().when(taskMapper).updateTaskFromDto(dto, task);
        when(taskRepository.save(task)).thenReturn(task);
        when(taskMapper.toDto(task)).thenReturn(expectedDto);

        //Act
        TaskResponseDTO result = taskService.updateTask(taskId, dto, user);

        //Assert
        assertEquals(expectedDto, result);
        verify(taskRepository).save(task);
        verify(taskMapper).updateTaskFromDto(dto, task);
        verify(taskMapper).toDto(task);
    }

    @Test
    @DisplayName("Check if a task can't be updated and throws a TaskNotFoundException")
    void testUpdateTask2() {
        //Arrange
        UUID taskId = UUID.randomUUID();
        User user = new User(UUID.randomUUID(), "usertest", "test", UserRole.USER);
        TaskRequestDTO dto = new TaskRequestDTO("Test task", "Task test for create task into TaskService", LocalDate.now().plusDays(5));

        when(taskRepository.findByIdAndUserId(taskId, user.getId())).thenReturn(null);

        //Act & Assert
        assertThrows(TaskNotFoundException.class, () -> {
            taskService.updateTask(taskId, dto, user);
        });

        verify(taskRepository, never()).save(any());
        verify(taskMapper, never()).toDto(any());
    }

    @Test
    @DisplayName("Check if a task is deleted")
    void testDeleteTask1() {
        //Arrange
        UUID taskId = UUID.randomUUID();
        User user = new User(UUID.randomUUID(), "usertest", "test", UserRole.USER);
        Task task = new Task(taskId, "Test task", "Task test for create task into TaskService", LocalDate.now().plusDays(5), false, user);

        when(taskRepository.findByIdAndUserId(taskId, user.getId())).thenReturn(task);

        //Act
        taskService.deleteTask(taskId, user);

        //Assert
        verify(taskRepository).delete(task);
    }

    @Test
    @DisplayName("Check if a task can't be deleted and throws a TaskNotFoundException")
    void testDeleteTask2() {
        //Arrange
        UUID taskId = UUID.randomUUID();
        User user = new User(UUID.randomUUID(), "usertest", "test", UserRole.USER);

        when(taskRepository.findByIdAndUserId(taskId, user.getId())).thenReturn(null);

        //Act & Assert
        assertThrows(TaskNotFoundException.class, () -> {
            taskService.deleteTask(taskId, user);
        });

        verify(taskRepository, never()).delete(any());
    }

    @Test
    @DisplayName("Check if a task can be set true into completed")
    void testCompletedTask1() {
        //Arrange
        UUID taskId = UUID.randomUUID();
        User user = new User(UUID.randomUUID(), "usertest", "test", UserRole.USER);
        Task task = new Task(taskId, "Test task", "Task test for create task into TaskService", LocalDate.now().plusDays(5), false, user);

        when(taskRepository.findByIdAndUserId(taskId, user.getId())).thenReturn(task);
        when(taskRepository.save(task)).thenReturn(task);

        //Act
        taskService.completedTask(taskId, user);

        //Assert
        assertTrue(task.getCompleted());
        verify(taskRepository).save(task);
    }

    @Test
    @DisplayName("Check if a task can't be set true into completed and throws a TaskNotFoundException")
    void testCompletedTask2() {
        //Arrange
        UUID taskId = UUID.randomUUID();
        User user = new User(UUID.randomUUID(), "usertest", "test", UserRole.USER);

        when(taskRepository.findByIdAndUserId(taskId, user.getId())).thenReturn(null);

        //Act & Assert
        assertThrows(TaskNotFoundException.class, () -> {
            taskService.completedTask(taskId, user);
        });

        verify(taskRepository, never()).save(any());
    }

    @Test
    @DisplayName("Check if a task can be found by its Id and return a TaskResponseDTO")
    void testSelectTask1() {
        //Arrange
        UUID taskId = UUID.randomUUID();
        User user = new User(UUID.randomUUID(), "usertest", "test", UserRole.USER);
        Task task = new Task(taskId, "Test task", "Task test for create task into TaskService", LocalDate.now().plusDays(5), false, user);
        TaskResponseDTO expectedDto = new TaskResponseDTO(task.getId(), task.getTitle(), task.getDescription(), task.getDueDate(), task.getCompleted());

        when(taskRepository.findByIdAndUserId(taskId, user.getId())).thenReturn(task);
        when(taskMapper.toDto(task)).thenReturn(expectedDto);

        //Act
        TaskResponseDTO result = taskService.selectTask(taskId, user);

        //Assert
        assertEquals(expectedDto, result);
        verify(taskRepository).findByIdAndUserId(taskId, user.getId());
        verify(taskMapper).toDto(task);
    }

    @Test
    @DisplayName("Check if a task can't be found by its Id and throws a TaskNotFoundException")
    void testSelectTask2() {
        //Arrange
        UUID taskId = UUID.randomUUID();
        User user = new User(UUID.randomUUID(), "usertest", "test", UserRole.USER);

        when(taskRepository.findByIdAndUserId(taskId, user.getId())).thenReturn(null);

        //Act & Assert
        assertThrows(TaskNotFoundException.class, () -> {
            taskService.selectTask(taskId, user);
        });

        verify(taskMapper, never()).toDto(any());
    }

    @Test
    @DisplayName("Check if a list of task uncompleted can be found for a User Id and return a list of TaskResponseDTO")
    void testSelectedTasks1() {
        //Arrange
        Boolean completed = false;
        User user = new User(UUID.randomUUID(), "usertest", "test", UserRole.USER);

        List<Task> tasks = List.of(
                new Task(UUID.randomUUID(), "Test task1", "Task test for create task into TaskService1", LocalDate.now().plusDays(5), completed, user),
                new Task(UUID.randomUUID(), "Test task2", "Task test for create task into TaskService2", LocalDate.now().plusDays(6), completed, user),
                new Task(UUID.randomUUID(), "Test task3", "Task test for create task into TaskService3", LocalDate.now().plusDays(7), completed, user)
        );

        List<TaskResponseDTO> expectedDto = tasks.stream()
                                                 .map(task -> new TaskResponseDTO(task.getId(), task.getTitle(), task.getDescription(), task.getDueDate(), task.getCompleted()))
                                                 .toList();

        when(taskRepository.findAllByUserIdAndCompleted(user.getId(), completed)).thenReturn(tasks);
        when(taskMapper.toDtoList(tasks)).thenReturn(expectedDto);

        //Act
        List<TaskResponseDTO> result = taskService.selectTasks(user, completed);

        //Assert
        assertEquals(expectedDto, result);
        verify(taskRepository).findAllByUserIdAndCompleted(user.getId(), completed);
        verify(taskMapper).toDtoList(tasks);
    }

    @Test
    @DisplayName("Check if a list of task completed can be found for a User Id and return a list of TaskResponseDTO")
    void testSelectedTasks2() {
        //Arrange
        Boolean completed = true;
        User user = new User(UUID.randomUUID(), "usertest", "test", UserRole.USER);

        List<Task> tasks = List.of(
                new Task(UUID.randomUUID(), "Test task1", "Task test for create task into TaskService1", LocalDate.now().plusDays(5), completed, user),
                new Task(UUID.randomUUID(), "Test task2", "Task test for create task into TaskService2", LocalDate.now().plusDays(6), completed, user),
                new Task(UUID.randomUUID(), "Test task3", "Task test for create task into TaskService3", LocalDate.now().plusDays(7), completed, user)
        );

        List<TaskResponseDTO> expectedDto = tasks.stream()
                .map(task -> new TaskResponseDTO(task.getId(), task.getTitle(), task.getDescription(), task.getDueDate(), task.getCompleted()))
                .toList();

        when(taskRepository.findAllByUserIdAndCompleted(user.getId(), completed)).thenReturn(tasks);
        when(taskMapper.toDtoList(tasks)).thenReturn(expectedDto);

        //Act
        List<TaskResponseDTO> result = taskService.selectTasks(user, completed);

        //Assert
        assertEquals(expectedDto, result);
        verify(taskRepository).findAllByUserIdAndCompleted(user.getId(), completed);
        verify(taskMapper).toDtoList(tasks);
    }

    @Test
    @DisplayName("Check if a list of task can't be found for a User Id and throws a TaskNotFoundException")
    void testSelectTasks3() {
        //Arrange
        Boolean completed = true;
        UUID taskId = UUID.randomUUID();
        User user = new User(UUID.randomUUID(), "usertest", "test", UserRole.USER);

        when(taskRepository.findAllByUserIdAndCompleted(user.getId(), completed)).thenReturn(List.of());

        //Act & Assert
        assertThrows(TaskNotFoundException.class, () -> {
            taskService.selectTasks(user, completed);
        });

        verify(taskMapper, never()).toDtoList(any());
    }
}