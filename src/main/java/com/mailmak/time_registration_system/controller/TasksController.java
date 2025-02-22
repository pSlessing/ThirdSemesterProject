package com.mailmak.time_registration_system.controller;

import com.mailmak.time_registration_system.classes.Role;
import com.mailmak.time_registration_system.classes.Task;
import com.mailmak.time_registration_system.dto.tasks.*;
import com.mailmak.time_registration_system.exceptions.ForbiddenException;
import com.mailmak.time_registration_system.mappers.TaskResponseMapper;
import com.mailmak.time_registration_system.service.TaskServiceInterface;
import com.mailmak.time_registration_system.service.UserServiceInterface;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/projects/{projectId}/tasks")
@Tag(name = "Tasks", description = "Endpoints for managing project tasks")
public class TasksController
{
    private final TaskServiceInterface taskService;
    private final UserServiceInterface userService;
    private final TaskResponseMapper taskResponseMapper;

    public TasksController(TaskServiceInterface taskService, UserServiceInterface userService, TaskResponseMapper taskResponseMapper) {
        this.taskService = taskService;
        this.userService = userService;
        this.taskResponseMapper = taskResponseMapper;
    }

    @Operation(summary = "Get filtered tasks", description = "Get tasks filtered by project id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tasks were successfully retrieved"),
            @ApiResponse(responseCode = "400", description = "Invalid request parameters"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Project not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/")
    public ResponseEntity<List<TaskResponse>> getTasks(@PathVariable UUID projectId, @AuthenticationPrincipal Jwt jwt)
    {
        try
        {
            userService.validateRequiredRoles(jwt, Role.EMPLOYEE, Role.MANAGER);
            ArrayList<Task> response = this.taskService.getTasks(projectId);

            return ResponseEntity.ok(response.stream().map(taskResponseMapper::mapTo).toList());
        }
        catch (ForbiddenException e)
        {
            return ResponseEntity.status(403).build();
        }
        catch (EntityNotFoundException e)
        {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Create task", description = "Create a new task for the project")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Task was successfully created"),
            @ApiResponse(responseCode = "400", description = "Invalid request parameters"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Project not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/")
    public ResponseEntity<TaskResponse> createTask(@PathVariable UUID projectId, @RequestBody CreateTaskRequest request, @AuthenticationPrincipal Jwt jwt)
    {
        try
        {
            request.setProjectId(projectId);
            userService.validateRequiredRoles(jwt, Role.MANAGER);

            Task response = this.taskService.createTask(request);

            return ResponseEntity.status(201).body(taskResponseMapper.mapTo(response));
        }
        catch (ForbiddenException e)
        {
            return ResponseEntity.status(403).build();
        }
        catch (EntityNotFoundException e)
        {
            return ResponseEntity.notFound().build();
        }
        catch (Exception e)
        {
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "Get task", description = "Get a task by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Task was successfully retrieved"),
            @ApiResponse(responseCode = "400", description = "Invalid request parameters"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Task not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/{taskId}")
    public ResponseEntity<TaskResponse> getTaskById(@PathVariable UUID taskId, @AuthenticationPrincipal Jwt jwt)
    {
        try
        {
            userService.validateRequiredRoles(jwt, Role.EMPLOYEE, Role.MANAGER);

            Task response = this.taskService.getTaskById(taskId);

            return ResponseEntity.ok(taskResponseMapper.mapTo(response));
        }
        catch (ForbiddenException e)
        {
            return ResponseEntity.status(403).build();
        }
        catch (EntityNotFoundException e)
        {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Update task", description = "Update a task by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Task was successfully updated"),
            @ApiResponse(responseCode = "400", description = "Invalid request parameters"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Task not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping("/{taskId}")
    public ResponseEntity<Void> updateTask(@PathVariable UUID taskId, @RequestBody UpdateTaskRequest request, @AuthenticationPrincipal Jwt jwt)
    {
        try
        {
            userService.validateRequiredRoles(jwt, Role.MANAGER);
            request.setTaskId(taskId);
            this.taskService.updateTask(request);

            // Return 204 No Content
            return ResponseEntity.noContent().build();
        }
        catch (ForbiddenException e)
        {
            // Return 403 Forbidden if the user does not have the required role
            return ResponseEntity.status(403).build();
        }
        catch (EntityNotFoundException e)
        {
            // Return 404 Not Found if the task is not found
            return ResponseEntity.notFound().build();
        }
        catch (Exception e)
        {
            // Return 400 Bad Request if the request is invalid
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "Delete task", description = "Delete a task by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Task was successfully deleted"),
            @ApiResponse(responseCode = "400", description = "Invalid request parameters"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Task not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @DeleteMapping("/{taskId}")
    public ResponseEntity<Void> deleteTask(@PathVariable UUID taskId, @AuthenticationPrincipal Jwt jwt)
    {
        try
        {
            userService.validateRequiredRoles(jwt, Role.MANAGER);
            this.taskService.deleteTask(taskId);

            return ResponseEntity.noContent().build();
        }
        catch (ForbiddenException e)
        {
            // Return 403 Forbidden if the user does not have the required role
            return ResponseEntity.status(403).build();
        }
        catch (EntityNotFoundException e)
        {
            // Return 404 Not Found if the task is not found
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Assign user to task", description = "Assign a single user to a task")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "User was successfully assigned to task"),
            @ApiResponse(responseCode = "400", description = "Invalid request parameters"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Task not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/{taskId}/users")
    public ResponseEntity<Void> assignUserToTask(@PathVariable UUID taskId, @RequestBody CreateUserTaskRequest request, @AuthenticationPrincipal Jwt jwt)
    {
        try
        {
            userService.validateRequiredRoles(jwt, Role.MANAGER);

            request.setTaskId(taskId);
            this.taskService.assignUserToTask(request);

            return ResponseEntity.noContent().build();
        }
        catch (ForbiddenException e)
        {
            return ResponseEntity.status(403).build();
        }
        catch (EntityNotFoundException e)
        {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Assign users to task", description = "Assign multiple users to a task")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Users were successfully assigned to task"),
            @ApiResponse(responseCode = "400", description = "Invalid request parameters"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Task not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/{taskId}/users-batch")
    public ResponseEntity<Void> assignUsersToTask(@PathVariable UUID taskId, @RequestBody CreateUserTasksBatchRequest request, @AuthenticationPrincipal Jwt jwt)
    {
        try
        {
            userService.validateRequiredRoles(jwt, Role.MANAGER);

            request.setTaskId(taskId);
            this.taskService.assignUsersToTask(request);

            return ResponseEntity.noContent().build();
        }
        catch (ForbiddenException e)
        {
            return ResponseEntity.status(403).build();
        }
        catch (EntityNotFoundException e)
        {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Delete user from task", description = "Delete a user from a task")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "User was successfully deleted from task"),
            @ApiResponse(responseCode = "400", description = "Invalid request parameters"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Task not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @DeleteMapping("/{taskId}/users")
    public ResponseEntity<Void> deleteUserFromTask(@PathVariable UUID taskId, @RequestBody DeleteUserTaskRequest request, @AuthenticationPrincipal Jwt jwt)
    {
        try
        {
            userService.validateRequiredRoles(jwt, Role.MANAGER);

            request.setTaskId(taskId);
            this.taskService.unassignUserFromTask(request);

            return ResponseEntity.noContent().build();
        }
        catch (ForbiddenException e)
        {
            return ResponseEntity.status(403).build();
        }
        catch (EntityNotFoundException e)
        {
            return ResponseEntity.notFound().build();
        }
        catch (Exception e)
        {
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "Delete users from task", description = "Delete multiple users from a task")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Users was successfully deleted from task"),
            @ApiResponse(responseCode = "400", description = "Invalid request parameters"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Task not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @DeleteMapping("/{taskId}/users-batch")
    public ResponseEntity<Void> deleteUsersFromTask(@PathVariable UUID taskId, @RequestBody DeleteUserTasksBatchRequest request, @AuthenticationPrincipal Jwt jwt)
    {
        try
        {
            userService.validateRequiredRoles(jwt, Role.MANAGER);

            request.setTaskId(taskId);
            this.taskService.unassignUsersFromTask(request);

            return ResponseEntity.noContent().build();
        }
        catch (ForbiddenException e)
        {
            return ResponseEntity.status(403).build();
        }
        catch (EntityNotFoundException e)
        {
            return ResponseEntity.notFound().build();
        }
    }
}
