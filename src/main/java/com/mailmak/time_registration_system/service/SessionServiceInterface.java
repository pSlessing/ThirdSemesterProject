package com.mailmak.time_registration_system.service;

import com.mailmak.time_registration_system.classes.*;
import com.mailmak.time_registration_system.dto.sessions.*;
import org.springframework.security.oauth2.jwt.Jwt;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface SessionServiceInterface {
    List<ProjectSession> getSessions(GetSessionRequest request);
    List<ProjectSession> getActiveSessions(LocalDateTime startedBefore);
    Session getSession(UUID sessionId);
    Session createSession(CreateSessionRequest request);
    void updateSession(UpdateSessionRequest request);
    void updateSessionsBatch(UpdateSessionsBatchRequest request, Jwt jwt);
    void deleteSession(UUID sessionId);
    void saveSession(ProjectSession session);
}
