package com.mailmak.time_registration_system.controller;

import com.mailmak.time_registration_system.classes.Role;
import com.mailmak.time_registration_system.classes.User;
import com.mailmak.time_registration_system.dto.users.UpdateUserRequest;
import com.mailmak.time_registration_system.dto.users.UpdateUserRequestTemp;
import com.mailmak.time_registration_system.dto.users.UserResponse;
import com.mailmak.time_registration_system.exceptions.ForbiddenException;
import com.mailmak.time_registration_system.mappers.UserResponseMapper;
import com.mailmak.time_registration_system.service.UserService;
import com.mailmak.time_registration_system.service.UserServiceInterface;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@Tag(name = "Users", description = "Endpoints for managing users")
public class UserController {

    private final UserServiceInterface userService;
    private final UserResponseMapper userMapper;

    public UserController(UserService userService, UserResponseMapper userMapper)
    {
        this.userService = userService;
        this.userMapper = userMapper;
    }

    @Operation(summary = "Get all users you have access to", description = "Returns every single user in the , that the requestMaker is allowed to see")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Users were successfully retrieved"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/")
    public ResponseEntity<List<UserResponse>> getAllUsers(@AuthenticationPrincipal Jwt jwt)
    {
        try
        {
            User currentUser = userService.getAuthorizedUser(jwt);

            if(currentUser.getRoles().equals(Role.MANAGER))
            {
                List<User> users = userService.getUsers();
                return ResponseEntity.ok(users.stream().map(userMapper::mapTo).toList());
            }
            else
            {
                userService.validateRequiredRoles(jwt, Role.EMPLOYEE, Role.MANAGER);

                List<UserResponse> users = new ArrayList<>();
                users.add(userMapper.mapTo(currentUser));
                return ResponseEntity.ok(users);
            }
        }
        catch (EntityNotFoundException e)
        {
            return ResponseEntity.notFound().build();
        }
        catch (ForbiddenException e)
        {
            return ResponseEntity.status(403).build();
        }
    }


    @Operation(summary = "Get a specific user via ID", description = "Gets a specific users information via their ID, if you're a manager")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tasks were successfully retrieved"),
            @ApiResponse(responseCode = "400", description = "Invalid request parameters"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/{userId}")
    public ResponseEntity<UserResponse> getUser(@AuthenticationPrincipal Jwt jwt, @PathVariable UUID userId)
    {
        try
        {
            User returnUser = userService.getUserById(userId)
                    .orElseThrow(() -> new EntityNotFoundException("User with ID " + userId + " not found"));

            if(returnUser.getPrincipalID().equals(UUID.fromString(jwt.getSubject())))
            {
                return ResponseEntity.ok(userMapper.mapTo(returnUser));
            }

            userService.validateRequiredRoles(jwt, Role.MANAGER);
            return ResponseEntity.ok(userMapper.mapTo(returnUser));

        }
        catch (EntityNotFoundException e)
        {
            return ResponseEntity.notFound().build();
        }
        catch (ForbiddenException e)
        {
            return ResponseEntity.status(403).build();
        }
    }


    @Operation(summary = "Update user information", description = "Updates the information of the user with the supplied information, disregarding ID and principal ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request parameters"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping("/{userId}")
    public ResponseEntity<UserResponse> editUser(@AuthenticationPrincipal Jwt jwt, @PathVariable UUID userId, @RequestBody UpdateUserRequest request)
    {
        try
        {
            System.out.println("Updating user: " + userId);
            System.out.println("Roles: " + request.getRoles());

            userService.validateRequiredRoles(jwt, Role.MANAGER);

            request.setUserId(userId);
            userService.updateUser(request);

            return ResponseEntity.noContent().build();
        }
        catch (EntityNotFoundException e)
        {
            System.out.println("Error while updating user: " + e.getMessage());
            return ResponseEntity.notFound().build();
        }
        catch (ForbiddenException e)
        {
            System.out.println("Error while updating user: " + e.getMessage());
            return ResponseEntity.status(403).build();
        }
        catch (Exception e)
        {
            System.out.println("Error while updating user: " + e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }


    @Operation(summary = "Delete user", description = "Delete the user from the database, if the user is is not found, it will still return code 200")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User is no longer in database"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @DeleteMapping("/{userId}")
    public ResponseEntity<UserResponse> deleteUser(@AuthenticationPrincipal Jwt jwt, @PathVariable UUID userId)
    {
        try
        {
            userService.validateRequiredRoles(jwt, Role.MANAGER);
            userService.deleteUser(userId);

            return ResponseEntity.ok().build();
        }
        catch (EntityNotFoundException e)
        {
            return ResponseEntity.notFound().build();
        }
        catch (ForbiddenException e)
        {
            return ResponseEntity.status(403).build();
        }
    }


    @Operation(summary = "Get information of user that is logged in", description = "Gets the information currently stored in the database, from the principal Id of the user currently logged in")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User information was successfully retrieved"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/me")
    public ResponseEntity<UserResponse> getUserSelf(@AuthenticationPrincipal Jwt jwt)
    {
        try
        {
            User currentUser = userService.getAuthorizedUser(jwt);
            return ResponseEntity.ok(userMapper.mapTo(currentUser));
        }
        catch (EntityNotFoundException e)
        {
            return ResponseEntity.notFound().build();
        }
    }
}
