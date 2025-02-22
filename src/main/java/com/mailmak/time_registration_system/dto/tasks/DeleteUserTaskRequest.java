package com.mailmak.time_registration_system.dto.tasks;

import lombok.Data;

import java.util.UUID;

@Data
public class DeleteUserTaskRequest {
    private UUID taskId;
    private UUID userId;
}
