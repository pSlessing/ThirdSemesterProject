package com.mailmak.time_registration_system.dto.tasks;

import lombok.Data;

import java.util.ArrayList;
import java.util.UUID;

@Data
public class DeleteUserTasksBatchRequest {
    private UUID taskId;
    private ArrayList<UUID> userIds;
}
