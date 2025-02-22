package com.mailmak.time_registration_system.controller;

import com.mailmak.time_registration_system.classes.OptOut;
import com.mailmak.time_registration_system.classes.Role;
import com.mailmak.time_registration_system.classes.User;
import com.mailmak.time_registration_system.dto.optouts.CreateUserOptOutRequest;
import com.mailmak.time_registration_system.dto.optouts.OptOutResponse;
import com.mailmak.time_registration_system.dto.optouts.UpdateUserOptOutRequest;
import com.mailmak.time_registration_system.exceptions.ForbiddenException;
import com.mailmak.time_registration_system.mappers.OptOutResponseMapper;
import com.mailmak.time_registration_system.service.OptOutServiceInterface;
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


import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users/{userId}/opt-outs")
@Tag(name = "Opt-Outs", description = "Endpoint for managing user Opt-Outs")
public class OptOutController {
    private final OptOutServiceInterface optOutService;
    private final UserServiceInterface userService;
    private final OptOutResponseMapper optOutResponseMapper;

    public OptOutController(OptOutServiceInterface optOutService, UserServiceInterface userService, OptOutResponseMapper optOutResponseMapper)
    {
        this.optOutService = optOutService;
        this.userService = userService;
        this.optOutResponseMapper = optOutResponseMapper;
    }

    @Operation(summary = "Get Opt-Outs", description = "Get Opt-Outs by user id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Opt-Outs were successfully retrieved"),
            @ApiResponse(responseCode = "400", description = "Invalid request parameters"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "User not found not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/")
    public ResponseEntity<List<OptOutResponse>> getOptOuts(@PathVariable UUID userId, @AuthenticationPrincipal Jwt jwt) {
        try {
            ValidateAuthenticatedUser(jwt, userId);

            List<OptOut> response = optOutService.getUserOptOuts(userId);

            return ResponseEntity.ok(response.stream().map(optOutResponseMapper::mapTo).toList());
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

    @Operation(summary = "Create OptOut", description = "Create a new OptOut for the user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "OptOut was successfully created"),
            @ApiResponse(responseCode = "400", description = "Invalid request parameters"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/")
    public ResponseEntity<OptOutResponse> CreateOptOut(@PathVariable UUID userId, @RequestBody CreateUserOptOutRequest request, @AuthenticationPrincipal Jwt jwt){
        try{
            ValidateAuthenticatedUser(jwt, userId);

            boolean userHasActiveOptOut = optOutService.userHasActiveOptOut(userId);
            if(userHasActiveOptOut){
                return ResponseEntity.badRequest().build();
            }

            OptOut optOut = optOutService.createOptOut(request, userId);
            return ResponseEntity.status(201).body(optOutResponseMapper.mapTo(optOut));
        }
        catch (EntityNotFoundException e) {
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

    @Operation(summary = "Update OptOut", description = "Update an OptOut by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "OptOut was successfully updated"),
            @ApiResponse(responseCode = "400", description = "Invalid request parameters"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "OptOut not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping("/{optOutId}")
    public ResponseEntity<Void> updateOptOut(@PathVariable UUID userId, @PathVariable UUID optOutId, @RequestBody UpdateUserOptOutRequest request, @AuthenticationPrincipal Jwt jwt){
        try{
            ValidateAuthenticatedUser(jwt, userId);

            LocalDateTime start = request.getPeriod().getStartDate();
            LocalDateTime end = request.getPeriod().getEndDate();

            if (start != null && end != null && start.isAfter(end)) {
                return ResponseEntity.badRequest().build();
            } else if (start == null && end != null &&
                    !optOutService.optOutStartsBefore(optOutId, request.getPeriod().getEndDate())) {
                return ResponseEntity.badRequest().build();
            }

            this.optOutService.updateOptOut(request, userId, optOutId);

            return ResponseEntity.noContent().build();
        }
        catch (EntityNotFoundException e) {
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

    @Operation(summary = "Delete OptOut", description = "Delete an OptOut by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "OptOut was successfully deleted"),
            @ApiResponse(responseCode = "400", description = "Invalid request parameters"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "OptOut not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @DeleteMapping("/{optOutId}")
    public ResponseEntity<Void> deleteOptOut(@PathVariable UUID userId, @PathVariable UUID optOutId, @AuthenticationPrincipal Jwt jwt){
        try{
            ValidateAuthenticatedUser(jwt, userId);

            this.optOutService.deleteOptOut(optOutId, userId);
            return ResponseEntity.noContent().build();
        }
        catch (EntityNotFoundException e) {
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

    private void ValidateAuthenticatedUser(Jwt jwt, UUID userId){
        User user = userService.getAuthorizedUser(jwt);
        if (user.getId().equals(userId)){
            userService.validateRequiredRoles(jwt, Role.EMPLOYEE, Role.MANAGER);
        }else{
            userService.validateRequiredRoles(jwt, Role.MANAGER);
        }
    }
}
