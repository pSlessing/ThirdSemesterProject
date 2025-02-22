package com.mailmak.time_registration_system.dto.sessions;

import java.util.UUID;

import com.mailmak.time_registration_system.classes.Period;
import com.mailmak.time_registration_system.classes.SessionState;
import lombok.Data;

@Data
public class CreateSessionRequest {
    private UUID userId;
    private UUID taskId;
    private Period period;
    private String description;
}
