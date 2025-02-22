package com.mailmak.time_registration_system.dto.sessions;

import java.util.UUID;

import com.mailmak.time_registration_system.classes.Period;
import com.mailmak.time_registration_system.classes.SessionState;
import com.mailmak.time_registration_system.classes.Task;
import com.mailmak.time_registration_system.classes.User;

import com.mailmak.time_registration_system.dto.tasks.TaskResponse;
import com.mailmak.time_registration_system.dto.users.UserResponse;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SessionResponse {
    private UUID id;
    private Period period;
    private int type;
    private String description;
    private SessionState state;
    private UserResponse user;
    private TaskResponse task;
}
