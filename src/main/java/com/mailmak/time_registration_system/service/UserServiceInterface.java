package com.mailmak.time_registration_system.service;

import com.mailmak.time_registration_system.classes.Role;
import com.mailmak.time_registration_system.classes.User;
import com.mailmak.time_registration_system.dto.users.UpdateUserRequest;
import com.mailmak.time_registration_system.dto.users.UserResponse;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserServiceInterface
{
    boolean userExists(UUID userId);
    void validateRequiredRoles(Jwt jwt, Role... requiredRoles);
    void validateRequiredRoles(User user, Role... requiredRoles);
    List<User> getUsers();
    User getAuthorizedUser(Jwt jwt);
    Optional<User> getUserById(UUID userId);
    User createUser(Jwt jwt);
    void updateUser(UpdateUserRequest request);
    void updateUserRole(UUID userId, int role);
    void deleteUser(UUID userId);
}
