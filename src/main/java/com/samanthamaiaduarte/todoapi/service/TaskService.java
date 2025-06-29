package com.samanthamaiaduarte.todoapi.service;

import com.samanthamaiaduarte.todoapi.domain.task.Task;
import com.samanthamaiaduarte.todoapi.domain.task.TaskRequestDTO;
import com.samanthamaiaduarte.todoapi.domain.task.TaskResponseDTO;
import com.samanthamaiaduarte.todoapi.domain.user.User;
import com.samanthamaiaduarte.todoapi.exception.TaskForbiddenException;
import com.samanthamaiaduarte.todoapi.exception.TaskNotFoundException;
import com.samanthamaiaduarte.todoapi.infra.mapper.TaskMapper;
import com.samanthamaiaduarte.todoapi.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private TaskMapper taskMapper;

    public TaskResponseDTO createTask(TaskRequestDTO data, User user) {
        Task task = taskMapper.toEntity(data, user);
        taskRepository.save(task);
        return taskMapper.toDto(task);
    }

    public TaskResponseDTO updateTask(UUID taskId, TaskRequestDTO data, User user) {
        Task task = taskRepository.findById(taskId).orElseThrow(TaskNotFoundException::new);

        if(task.getUser().getId() != user.getId()) throw new TaskForbiddenException();

        taskMapper.updateTaskFromDto(data, task);
        taskRepository.save(task);
        return taskMapper.toDto(task);
    }

    public void deleteTask(UUID taskId, User user) {
        Task task = taskRepository.findByIdAndUserId(taskId, user.getId());

        if(task == null) throw new TaskNotFoundException();
        taskRepository.delete(task);
    }

    public void completedTask(UUID taskId, User user) {
        Task task = taskRepository.findByIdAndUserId(taskId, user.getId());
        if(task == null) throw new TaskNotFoundException();

        task.setCompleted(true);
        taskRepository.save(task);
    }

    public TaskResponseDTO selectTask(UUID taskId, User user) {
        Task task = taskRepository.findByIdAndUserId(taskId, user.getId());

        if(task == null) throw new TaskNotFoundException();
        return taskMapper.toDto(task);
    }

    public List<TaskResponseDTO> selectTasks(User user, Boolean completed) {
        List<Task> tasks = taskRepository.findAllByUserIdAndCompleted(user.getId(), completed);

        if(tasks.isEmpty()) throw new TaskNotFoundException();
        return taskMapper.toDtoList(tasks);
    }
}
