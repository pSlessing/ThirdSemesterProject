package com.mailmak.time_registration_system.controller;

import com.mailmak.time_registration_system.classes.Comment;
import com.mailmak.time_registration_system.classes.Role;
import com.mailmak.time_registration_system.classes.User;
import com.mailmak.time_registration_system.dto.comments.CommentResponse;
import com.mailmak.time_registration_system.dto.comments.CreateCommentRequest;
import com.mailmak.time_registration_system.dto.comments.UpdateCommentRequest;
import com.mailmak.time_registration_system.exceptions.ForbiddenException;
import com.mailmak.time_registration_system.mappers.CommentResponseMapper;
import com.mailmak.time_registration_system.service.CommentServiceInterface;
import com.mailmak.time_registration_system.service.UserServiceInterface;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;


import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/projects/{projectId}/tasks/{taskId}/comments")
@Tag(name = "Comments", description = "Endpoints for managing comments.")
public class CommentController {

    private final CommentServiceInterface commentService;
    private final UserServiceInterface userService;
    private final CommentResponseMapper commentResponseMapper;

    public CommentController(CommentServiceInterface commentService, UserServiceInterface userService, CommentResponseMapper commentResponseMapper) {
        this.commentService = commentService;
        this.userService = userService;
        this.commentResponseMapper = commentResponseMapper;
    }

    @Operation(summary = "Create a comment", description = "Add a new comment to a task.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Comment successfully created"),
            @ApiResponse(responseCode = "400", description = "Invalid request")
    })
    @PostMapping("/")
    public ResponseEntity<CommentResponse> createComment(@PathVariable UUID taskId, @RequestBody CreateCommentRequest request, @AuthenticationPrincipal Jwt jwt) {
        try {
            User user = userService.getAuthorizedUser(jwt);
            userService.validateRequiredRoles(user, Role.EMPLOYEE, Role.MANAGER);

            request.setUserId(user.getId());
            request.setTaskId(taskId);
            Comment comment = commentService.createComment(request);
            return ResponseEntity.status(201).body(commentResponseMapper.mapTo(comment));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
        catch (ForbiddenException e)
        {
            return ResponseEntity.status(403).build();
        }
        catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    @Operation(summary = "Get comments by task ID", description = "Retrieve all comments for a specific task.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Comments successfully retrieved"),
            @ApiResponse(responseCode = "404", description = "Task not found")
    })
    @GetMapping("/")
    public ResponseEntity<List<CommentResponse>> getComments(@PathVariable UUID taskId, @AuthenticationPrincipal Jwt jwt) {
        try {
            userService.validateRequiredRoles(jwt, Role.EMPLOYEE, Role.MANAGER);

            List<Comment> responses = commentService.getComments(taskId);
            return ResponseEntity.ok(responses.stream().map(commentResponseMapper::mapTo).toList());
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
        catch (ForbiddenException e)
        {
            return ResponseEntity.status(403).build();
        }
        catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    @Operation(summary = "Update a comment", description = "Update an existing comment.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Comment successfully updated"),
            @ApiResponse(responseCode = "404", description = "Comment not found")
    })
    @PutMapping("/{commentId}")
    public ResponseEntity<Void> updateComment(@PathVariable UUID commentId, @RequestBody UpdateCommentRequest request, @AuthenticationPrincipal Jwt jwt) {
        try {
            userService.validateRequiredRoles(jwt, Role.EMPLOYEE, Role.MANAGER);

            request.setCommentId(commentId);
            commentService.updateComment(request);

            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
        catch (ForbiddenException e)
        {
            return ResponseEntity.status(403).build();
        }
        catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    @Operation(summary = "Delete a comment", description = "Delete a comment by ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Comment successfully deleted"),
            @ApiResponse(responseCode = "404", description = "Comment not found")
    })
    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable UUID commentId, @AuthenticationPrincipal Jwt jwt) {
        try {
            userService.validateRequiredRoles(jwt, Role.EMPLOYEE, Role.MANAGER);

            commentService.deleteComment(commentId);

            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
        catch (ForbiddenException e)
        {
            return ResponseEntity.status(403).build();
        }
        catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }
}
