package com.mailmak.time_registration_system.dto.sessions;

import java.util.UUID;

import com.mailmak.time_registration_system.classes.Period;
import com.mailmak.time_registration_system.classes.SessionState;

import lombok.Builder;
import lombok.Data;

@Data
public class UpdateSessionRequest {
    private UUID sessionId;
    private Period period;    
    private String description;
    private SessionState state;
}
