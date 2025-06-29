package com.samanthamaiaduarte.todoapi.infra.mapper;

import com.samanthamaiaduarte.todoapi.domain.task.Task;
import com.samanthamaiaduarte.todoapi.domain.task.TaskRequestDTO;
import com.samanthamaiaduarte.todoapi.domain.task.TaskResponseDTO;
import com.samanthamaiaduarte.todoapi.domain.user.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper
public interface TaskMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "completed", constant = "false")
    @Mapping(target = "user", source = "user")
    Task toEntity(TaskRequestDTO task, User user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "completed", ignore = true)
    @Mapping(target = "user", ignore = true)
    void updateTaskFromDto(TaskRequestDTO dto, @MappingTarget Task task);

    TaskResponseDTO toDto(Task task);

    List<TaskResponseDTO> toDtoList(List<Task> tasks);
}
