package com.mailmak.time_registration_system.mappers;

import com.mailmak.time_registration_system.classes.ProjectSession;
import com.mailmak.time_registration_system.classes.Session;
import com.mailmak.time_registration_system.dto.sessions.SessionResponse;
import com.mailmak.time_registration_system.dto.tasks.TaskResponse;
import com.mailmak.time_registration_system.dto.users.UserResponse;
import org.springframework.stereotype.Component;

@Component
public class SessionResponseMapper implements ModelMapper<Session, SessionResponse> {

    public SessionResponseMapper() {}

    @Override
    public SessionResponse mapTo(Session session) {
        return SessionResponse.builder()
                .id(session.getId())
                .period(session.getPeriod())
                .type(session instanceof ProjectSession ? 1 : 0)
                .state(session instanceof ProjectSession ? ((ProjectSession) session).getState() : null)
                .description(session instanceof ProjectSession ? ((ProjectSession) session).getDescription() : null)
                .task(session instanceof ProjectSession
                        ? new TaskResponseMapper().mapTo(((ProjectSession) session).getTask())
                        : null)
                .user(UserResponse.builder()
                        .id(session.getUser().getId())
                        .name(session.getUser().getName())
                        .email(session.getUser().getEmail())
                        .roles(session.getUser().getRoles())
                        .build())
                .build();
    }
}
