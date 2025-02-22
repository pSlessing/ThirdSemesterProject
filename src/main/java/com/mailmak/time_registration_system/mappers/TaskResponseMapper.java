package com.mailmak.time_registration_system.mappers;

import com.mailmak.time_registration_system.classes.CompletableTask;
import com.mailmak.time_registration_system.classes.Task;
import com.mailmak.time_registration_system.dto.tasks.TaskResponse;
import com.mailmak.time_registration_system.dto.users.UserResponse;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.stream.Collectors;

@Component
public class TaskResponseMapper implements ModelMapper<Task, TaskResponse> {

    public TaskResponseMapper() {}

    @Override
    public TaskResponse mapTo(Task task) {
        return TaskResponse.builder()
                .id(task.getId())
                .name(task.getName())
                .description(task.getDescription())
                .project(task.getProject() != null ? new ProjectResponseMapper().mapTo(task.getProject()) : null)
                .state(task instanceof CompletableTask ? ((CompletableTask) task).getState() : null)
                .assignedUsers(task.getAssignedUsers() != null ? task.getAssignedUsers().stream()
                        .map(user -> UserResponse.builder()
                                .id(user.getId())
                                .name(user.getName())
                                .email(user.getEmail())
                                .roles(user.getRoles())
                                .build())
                        .collect(Collectors.toList()) : new ArrayList<>(0))
                .build();
    }
}
