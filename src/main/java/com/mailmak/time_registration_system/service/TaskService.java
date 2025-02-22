package com.mailmak.time_registration_system.service;

import com.mailmak.time_registration_system.classes.*;
import com.mailmak.time_registration_system.dto.tasks.*;
import com.mailmak.time_registration_system.dto.users.UserResponse;
import com.mailmak.time_registration_system.repository.ProjectRepository;
import com.mailmak.time_registration_system.repository.TaskRepository;
import com.mailmak.time_registration_system.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.UUID;

@Service
public class TaskService implements TaskServiceInterface
{
    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final UserService userService;
    private final UserRepository userRepository;

    @Autowired
    public TaskService(UserService userService, TaskRepository taskRepository, ProjectRepository projectRepository, UserRepository userRepository)
    {
        this.taskRepository = taskRepository;
        this.projectRepository = projectRepository;
        this.userService = userService;
        this.userRepository = userRepository;
    }

    @Override
    public ArrayList<Task> getTasks(UUID projectId)
    {
        return this.taskRepository.findByProjectId(projectId);
    }

    @Override
    public Task getTaskById(UUID taskId)
    {
        return this.taskRepository.findById(taskId)
                .orElseThrow(() -> new EntityNotFoundException("Task with ID " + taskId + " not found"));
    }

    @Override
    public Task createTask(CreateTaskRequest request)
    {
        Project project = this.projectRepository.findById(request.getProjectId())
                .orElseThrow(() -> new EntityNotFoundException("Project ID " + request.getProjectId() + " not found"));

        Task task = request.getTaskType() == 0
                ? CreateRecurringTask(request, project)
                : CreateCompletableTask(request, project);

        this.taskRepository.save(task);

        return task;
    }

    public Task CreateRecurringTask(CreateTaskRequest request, Project project)
    {
        return RecurringTask.builder()
                .name(request.getName())
                .description(request.getDescription())
                .project(project)
                .build();
    }

    public Task CreateCompletableTask(CreateTaskRequest request, Project project)
    {
        CompletableTask task = CompletableTask.builder()
                .name(request.getName())
                .description(request.getDescription())
                .project(project)
                .state(TaskState.PENDING)
                .build();

        if (request.getDeadline() != null)
        {
            task.setDeadline(request.getDeadline());
        }

        return task;
    }

    @Override
    public void updateTask(UpdateTaskRequest request)
    {
        Task task = this.taskRepository.findById(request.getTaskId())
                .orElseThrow(() -> new EntityNotFoundException("Task with ID " + request.getTaskId() + " not found"));

        if (request.getName() != null)
        {
            task.setName(request.getName());
        }

        if (request.getDescription() != null)
        {
            task.setDescription(request.getDescription());
        }

        if (request.getDeadline() != null && task instanceof CompletableTask)
        {
            ((CompletableTask) task).setDeadline(request.getDeadline());
        }

        if (request.getState() != null && task instanceof CompletableTask)
        {
            ((CompletableTask) task).setState(request.getState());
        }

        this.taskRepository.save(task);
    }

    @Override
    public void deleteTask(UUID id)
    {
        if (taskRepository.existsById(id)) {
            taskRepository.deleteById(id);
        } else {
            throw new EntityNotFoundException("Task with ID " + id + " not found");
        }
    }

    @Override
    public void assignUserToTask(CreateUserTaskRequest request)
    {
        Task task = this.taskRepository.findById(request.getTaskId())
                .orElseThrow(() -> new EntityNotFoundException("Task with ID " + request.getTaskId() + " not found"));

        User user = this.userRepository.findById(request.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("User with ID " + request.getUserId() + " not found"));

        if (user.getAssignedTasks().contains(task))
        {
            throw new IllegalArgumentException("User with ID " + request.getUserId() + " is already assigned to task with ID " + request.getTaskId());
        }

        user.getAssignedTasks().add(task);

        this.userRepository.save(user);
    }

    @Override
    public void assignUsersToTask(CreateUserTasksBatchRequest request)
    {
        Task task = this.taskRepository.findById(request.getTaskId())
                .orElseThrow(() -> new EntityNotFoundException("Task with ID " + request.getTaskId() + " not found"));

        for (UUID userId : request.getUserIds())
        {
            User user = this.userRepository.findById(userId)
                    .orElseThrow(() -> new EntityNotFoundException("User with ID " + userId + " not found"));

            if (user.getAssignedTasks().contains(task))
            {
                throw new IllegalArgumentException("User with ID " + userId + " is already assigned to task with ID " + request.getTaskId());
            }

            user.getAssignedTasks().add(task);

            this.userRepository.save(user);
        }
    }

    @Override
    public void unassignUserFromTask(DeleteUserTaskRequest request)
    {
        Task task = this.taskRepository.findById(request.getTaskId())
                .orElseThrow(() -> new EntityNotFoundException("Task with ID " + request.getTaskId() + " not found"));

        User user = this.userRepository.findById(request.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("User with ID " + request.getUserId() + " not found"));

        if (!user.getAssignedTasks().contains(task))
        {
            throw new IllegalArgumentException("User with ID " + request.getUserId() + " is not assigned to task with ID " + request.getTaskId());
        }

        user.getAssignedTasks().remove(task);

        this.userRepository.save(user);
    }

    @Override
    public void unassignUsersFromTask(DeleteUserTasksBatchRequest request) {
        Task task = this.taskRepository.findById(request.getTaskId())
                .orElseThrow(() -> new EntityNotFoundException("Task with ID " + request.getTaskId() + " not found"));

        for (UUID userId : request.getUserIds()) {
            User user = this.userRepository.findById(userId)
                    .orElseThrow(() -> new EntityNotFoundException("User with ID " + userId + " not found"));

            if (!user.getAssignedTasks().contains(task))
            {
                throw new IllegalArgumentException("User with ID " + userId + " is not assigned to task with ID " + request.getTaskId());
            }

            user.getAssignedTasks().remove(task);

            this.userRepository.save(user);
        }
    }
}
