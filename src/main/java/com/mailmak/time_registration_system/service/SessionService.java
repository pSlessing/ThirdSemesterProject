package com.mailmak.time_registration_system.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.oauth2.jwt.Jwt;

import com.mailmak.time_registration_system.classes.*;
import com.mailmak.time_registration_system.dto.sessions.CreateSessionRequest;
import com.mailmak.time_registration_system.dto.sessions.GetSessionRequest;
import com.mailmak.time_registration_system.dto.sessions.SessionResponse;
import com.mailmak.time_registration_system.dto.sessions.UpdateSessionRequest;
import com.mailmak.time_registration_system.dto.sessions.UpdateSessionsBatchRequest;
import com.mailmak.time_registration_system.dto.users.UserResponse;
import com.mailmak.time_registration_system.repository.SessionRepository;
import com.mailmak.time_registration_system.repository.TaskRepository;
import com.mailmak.time_registration_system.specification.SessionSpecification;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class SessionService implements SessionServiceInterface {
    private final SessionRepository sessionRepository;
    private final UserServiceInterface userService;
    private final TaskRepository taskRepository;

    @Autowired
    public SessionService(SessionRepository sessionRepository, UserServiceInterface userService, TaskRepository taskRepository) {
        this.sessionRepository = sessionRepository;
        this.userService = userService;
        this.taskRepository = taskRepository;
    }

    @Override
    public List<ProjectSession> getSessions(GetSessionRequest request) {

        Specification<ProjectSession> specification = Specification.where(SessionSpecification.filter(request));

        return sessionRepository.findAll(specification);
    }

    @Override
    public List<ProjectSession> getActiveSessions(LocalDateTime startedBefore)
    {
        Specification<ProjectSession> specification = Specification.where(SessionSpecification.isActive(startedBefore));
        return sessionRepository.findAll(specification);
    }

    @Override
    public Session getSession(UUID sessionId) {
        return this.sessionRepository.findById(sessionId)
                .orElseThrow(() -> new EntityNotFoundException("Session with ID " + sessionId + " not found"));
    }

    @Override
    public Session createSession(CreateSessionRequest request) {
        User user = this.userService.getUserById(request.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("User with ID " + request.getUserId() + " not found"));

        Task task = this.taskRepository.findById(request.getTaskId())
                .orElseThrow(() -> new EntityNotFoundException("Task with ID " + request.getTaskId() + " not found"));

        Period period = Period.builder()
                .startDate(request.getPeriod().getStartDate() == null ? LocalDateTime.now() : request.getPeriod().getStartDate())
                .endDate(request.getPeriod().getEndDate())
                .build();

        ProjectSession session = ProjectSession.builder()
                .user(user)
                .period(period)
                .task(task)
                .state(SessionState.ACTIVE)
                .description(request.getDescription())
                .build();

        return this.sessionRepository.save(session);
    }

    @Override
    public void updateSession(UpdateSessionRequest request) {
        ProjectSession session = this.sessionRepository.findById(request.getSessionId())
                .orElseThrow(() -> new EntityNotFoundException("Session with ID " + request.getSessionId() + " not found"));
        
        if (request.getState() != null) {
            session.setState(request.getState());
        }

        if (request.getDescription() != null) {
            session.setDescription(request.getDescription());
        }

        if (request.getPeriod() != null) {
            if (request.getPeriod().getStartDate() != null) {
                session.getPeriod().setStartDate(request.getPeriod().getStartDate());
            }

            if (request.getPeriod().getEndDate() != null) {
                session.getPeriod().setEndDate(request.getPeriod().getEndDate());
            }
        }

        this.sessionRepository.save(session);
    }

    @Override
    public void updateSessionsBatch(UpdateSessionsBatchRequest request, Jwt jwt) {
        User user = userService.getAuthorizedUser(jwt);

        List<ProjectSession> sessionsToSave = new ArrayList<>();

        for (UUID sessionId : request.getSessionIds()) {
            ProjectSession session = this.sessionRepository.findById(sessionId)
                    .orElseThrow(() -> new EntityNotFoundException("Session with ID " + sessionId + " not found"));

            UUID userId = session.getUser().getId();
            if (user.getId().equals(userId)) {
                userService.validateRequiredRoles(jwt, Role.EMPLOYEE, Role.MANAGER);
            } else {
                userService.validateRequiredRoles(jwt, Role.MANAGER);
            }

            if (session.getState() != SessionState.COMPLETED) {
                throw new IllegalStateException("Session with ID " + sessionId + " is not completed");
            }

            session.setState(SessionState.INVOICED);

            sessionsToSave.add(session);
        }

        this.sessionRepository.saveAll(sessionsToSave);
    }

    @Override
    public void deleteSession(UUID sessionId) {
        if (sessionRepository.existsById(sessionId)) {
            sessionRepository.deleteById(sessionId);
        } else {
            throw new EntityNotFoundException("Task with ID " + sessionId + " not found");
        }
    }

    @Override
    public void saveSession(ProjectSession session) {
        this.sessionRepository.save(session);
    }
}
