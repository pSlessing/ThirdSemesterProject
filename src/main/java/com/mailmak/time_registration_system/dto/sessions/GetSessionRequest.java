package com.mailmak.time_registration_system.dto.sessions;

import java.time.LocalDateTime;
import java.util.UUID;

import com.mailmak.time_registration_system.classes.SessionState;

import lombok.*;

@Getter
@Setter
public class GetSessionRequest {
    private UUID customerId;
    private UUID projectId;
    private UUID taskId;
    private UUID userId;
    private SessionState state;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
}