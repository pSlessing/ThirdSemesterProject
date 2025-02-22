package com.mailmak.time_registration_system.dto.tasks;

import lombok.Getter;

import java.util.UUID;

@Getter
public class GetTasksRequest {
    public UUID projectId;
}
