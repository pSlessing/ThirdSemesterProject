package com.mailmak.time_registration_system.dto.tasks;

import com.mailmak.time_registration_system.classes.Project;
import com.mailmak.time_registration_system.classes.TaskState;
import com.mailmak.time_registration_system.dto.projects.ProjectResponse;
import com.mailmak.time_registration_system.dto.users.UserResponse;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
@Builder
public class TaskResponse {
    private UUID id;
    private String name;
    private String description;
    private TaskState state;
    private ProjectResponse project;

    private List<UserResponse> assignedUsers;
}
