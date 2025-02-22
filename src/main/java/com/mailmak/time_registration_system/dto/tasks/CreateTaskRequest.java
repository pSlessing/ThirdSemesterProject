package com.mailmak.time_registration_system.dto.tasks;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class CreateTaskRequest {
    private UUID projectId;
    private int taskType;
    private String name;
    private String description;
    private LocalDateTime deadline;
}
