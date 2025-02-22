package com.mailmak.time_registration_system.dto.tasks;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
public class CreateUserTaskRequest {
    private UUID taskId;
    private UUID userId;
}
