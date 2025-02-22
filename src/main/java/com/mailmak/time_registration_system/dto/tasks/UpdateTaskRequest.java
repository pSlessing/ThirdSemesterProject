package com.mailmak.time_registration_system.dto.tasks;

import com.mailmak.time_registration_system.classes.TaskState;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class UpdateTaskRequest {
    private UUID taskId;
    private String name;
    private String description;
    private LocalDateTime deadline;
    private TaskState state;
}
