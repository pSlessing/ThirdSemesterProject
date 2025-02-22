package com.mailmak.time_registration_system.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.mailmak.time_registration_system.classes.*;
import com.mailmak.time_registration_system.mappers.SessionResponseMapper;
import com.mailmak.time_registration_system.service.SessionServiceInterface;
import com.mailmak.time_registration_system.service.UserServiceInterface;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.mailmak.time_registration_system.dto.sessions.CreateSessionRequest;
import com.mailmak.time_registration_system.dto.sessions.GetSessionRequest;
import com.mailmak.time_registration_system.dto.sessions.SessionResponse;
import com.mailmak.time_registration_system.dto.sessions.UpdateSessionRequest;
import com.mailmak.time_registration_system.dto.sessions.UpdateSessionsBatchRequest;
import com.mailmak.time_registration_system.exceptions.ForbiddenException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.oauth2.jwt.Jwt;

@RestController
@RequestMapping("/api/sessions")
@Tag(name = "Sessions", description = "Endpoints for managing sessions.")
public class SessionController {
    private final UserServiceInterface userService;
    private final SessionServiceInterface sessionService;
    private final SessionResponseMapper sessionResponseMapper;

    public SessionController(UserServiceInterface userService, SessionServiceInterface sessionService, SessionResponseMapper sessionResponseMapper) {
        this.userService = userService;
        this.sessionService = sessionService;
        this.sessionResponseMapper = sessionResponseMapper;
    }

    @Operation(summary = "Get filtered sessions", description = "Get sessions filtered by request") 
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Sessions were successfully retrieved"),
        @ApiResponse(responseCode = "400", description = "Invalid request parameters"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden"),
        @ApiResponse(responseCode = "404", description = "Project not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/")
    public ResponseEntity<List<SessionResponse>> getSessions(
        @RequestParam(required = false) UUID customerId, 
        @RequestParam(required = false) UUID projectId, 
        @RequestParam(required = false) UUID taskId, 
        @RequestParam(required = false) UUID userId, 
        @RequestParam(required = false) SessionState state, 
        @RequestParam(required = false) LocalDateTime startDate, 
        @RequestParam(required = false) LocalDateTime endDate, 
        @AuthenticationPrincipal Jwt jwt)
    {
        try
        {
            User authorizedUser = userService.getAuthorizedUser(jwt);

            if (userId != authorizedUser.getId()) {
                userService.validateRequiredRoles(authorizedUser, Role.MANAGER);
            } else {
                userService.validateRequiredRoles(authorizedUser, Role.EMPLOYEE, Role.MANAGER);
            }

            GetSessionRequest request = new GetSessionRequest();
            request.setCustomerId(customerId);
            request.setProjectId(projectId);
            request.setTaskId(taskId);
            request.setUserId(userId);
            request.setState(state);
            request.setStartDate(startDate);
            request.setEndDate(endDate);
            
            List<ProjectSession> response = this.sessionService.getSessions(request);

            return ResponseEntity.ok(response.stream().map(sessionResponseMapper::mapTo).toList());
        }
        catch (ForbiddenException e)
        {
            return ResponseEntity.status(403).build();
        }
        catch (EntityNotFoundException e)
        {
            return ResponseEntity.notFound().build();
        }
    }


    @Operation(summary = "Get session", description = "Get a session by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Session was successfully retrieved"),
            @ApiResponse(responseCode = "400", description = "Invalid request parameters"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Session not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/{sessionId}")
    public ResponseEntity<SessionResponse> getSession(@PathVariable UUID sessionId, @AuthenticationPrincipal Jwt jwt)
    {
        try
        {
            Session session = sessionService.getSession(sessionId);

            UUID userId = session.getUser().getId();

            validateAuthenticatedUser(jwt, userId);

            return ResponseEntity.ok(sessionResponseMapper.mapTo(session));
        }
        catch (ForbiddenException e)
        {
            return ResponseEntity.status(403).build();
        }
        catch (EntityNotFoundException e)
        {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Create session", description = "Create a new session for the project")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Session was successfully created"),
            @ApiResponse(responseCode = "400", description = "Invalid request parameters"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Project not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/")
    public ResponseEntity<SessionResponse> createSession(@RequestBody CreateSessionRequest request, @AuthenticationPrincipal Jwt jwt)
    {
        try
        {
            UUID userId = request.getUserId();

            validateAuthenticatedUser(jwt, userId);

            Session response = this.sessionService.createSession(request);

            return ResponseEntity.status(201).body(sessionResponseMapper.mapTo(response));
        }
        catch (ForbiddenException e)
        {
            return ResponseEntity.status(403).build();
        }
        catch (EntityNotFoundException e)
        {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Update session", description = "Update a session by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Session was successfully updated"),
            @ApiResponse(responseCode = "400", description = "Invalid request parameters"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Session not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping("/{sessionId}")
    public ResponseEntity<Void> updateSession(@PathVariable UUID sessionId, @RequestBody UpdateSessionRequest request, @AuthenticationPrincipal Jwt jwt)
    {
        try
        {
            Session session = sessionService.getSession(sessionId);

            UUID userId = session.getUser().getId();

            validateAuthenticatedUser(jwt, userId);

            request.setSessionId(sessionId);
            this.sessionService.updateSession(request);

            return ResponseEntity.noContent().build();
        }
        catch (ForbiddenException e)
        {
            return ResponseEntity.status(403).build();
        }
        catch (EntityNotFoundException e)
        {
            return ResponseEntity.notFound().build();
        }
        catch (Exception e)
        {
            return ResponseEntity.status(500).build();
        }
    }

    @Operation(summary = "Assign states to sessions", description = "Assign states to multiple sessions")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "States were successfully changed in sessions"),
            @ApiResponse(responseCode = "400", description = "Invalid request parameters"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Task not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping("/")
    public ResponseEntity<Void> updateSessionsState(@RequestBody UpdateSessionsBatchRequest request, @AuthenticationPrincipal Jwt jwt)
    {
        try
        {
            this.sessionService.updateSessionsBatch(request, jwt);

            return ResponseEntity.noContent().build();
        }
        catch (ForbiddenException e)
        {
            return ResponseEntity.status(403).build();
        }
        catch (EntityNotFoundException e)
        {
            return ResponseEntity.notFound().build();
        }
        catch (Exception e)
        {
            return ResponseEntity.status(500).build();
        }
    }

    @Operation(summary = "Delete session", description = "Delete a session by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Session was successfully deleted"),
            @ApiResponse(responseCode = "400", description = "Invalid request parameters"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "Task not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @DeleteMapping("/{sessionId}")
    public ResponseEntity<Void> deleteTask(@PathVariable UUID sessionId, @AuthenticationPrincipal Jwt jwt)
    {
        try
        {
            Session session = sessionService.getSession(sessionId);

            UUID userId = session.getUser().getId();

            validateAuthenticatedUser(jwt, userId);
            this.sessionService.deleteSession(sessionId);

            return ResponseEntity.noContent().build();
        }
        catch (ForbiddenException e)
        {
            return ResponseEntity.status(403).build();
        }
        catch (EntityNotFoundException e)
        {
            return ResponseEntity.notFound().build();
        }
    }

    private void validateAuthenticatedUser(Jwt jwt, UUID userId){
        User user = userService.getAuthorizedUser(jwt);
        if (user.getId().equals(userId)) {
            userService.validateRequiredRoles(jwt, Role.EMPLOYEE, Role.MANAGER);
        } else {
            userService.validateRequiredRoles(jwt, Role.MANAGER);
        }
    }
}
