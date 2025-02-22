package com.mailmak.time_registration_system.service;

import com.mailmak.time_registration_system.classes.*;
import com.mailmak.time_registration_system.dto.tasks.*;

import java.util.ArrayList;
import java.util.UUID;

public interface TaskServiceInterface {
    ArrayList<Task> getTasks(UUID projectId);
    Task getTaskById(UUID taskId);
    Task createTask(CreateTaskRequest request);
    void updateTask(UpdateTaskRequest request);
    void deleteTask(UUID id);
    void assignUserToTask(CreateUserTaskRequest request);
    void assignUsersToTask(CreateUserTasksBatchRequest request);
    void unassignUserFromTask(DeleteUserTaskRequest request);
    void unassignUsersFromTask(DeleteUserTasksBatchRequest request);
}
