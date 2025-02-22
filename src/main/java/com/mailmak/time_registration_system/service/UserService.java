package com.mailmak.time_registration_system.service;

import com.mailmak.time_registration_system.classes.Role;
import com.mailmak.time_registration_system.classes.User;
import com.mailmak.time_registration_system.dto.users.UpdateUserRequest;
import com.mailmak.time_registration_system.dto.users.UserResponse;
import com.mailmak.time_registration_system.exceptions.ForbiddenException;
import com.mailmak.time_registration_system.mappers.UserResponseMapper;
import com.mailmak.time_registration_system.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService implements UserServiceInterface {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public boolean userExists(UUID userId) {
        Optional<User> returnEntity = this.userRepository.findById(userId);

        return returnEntity.isPresent();
    }

    @Override
    public void validateRequiredRoles(Jwt jwt, Role... requiredRoles) {
        UUID principalId = UUID.fromString(jwt.getSubject());
        User user = this.userRepository.findByPrincipalID(principalId)
                .orElseThrow(() -> new EntityNotFoundException("User with Principal ID " + jwt.getSubject() + " not found"));

        validateRequiredRoles(user, requiredRoles);
    }

    @Override
    public List<User> getUsers() {
        return this.userRepository.findAll();
    }

    @Override
    public User getAuthorizedUser(Jwt jwt) {
        String subject = jwt.getSubject();
        UUID principal = UUID.fromString(subject);

        Optional<User> authenticatedUser = this.userRepository.findByPrincipalID(principal);

        return authenticatedUser.orElseGet(() -> createUser(jwt));
    }

    @Override
    public void validateRequiredRoles(User user, Role... requiredRoles) {

        if (!Role.hasRole(user.getRoles(), requiredRoles)) {
            throw new ForbiddenException("You do not have the required roles to access this resource");
        }
    }
    @Override
    public Optional<User> getUserById(UUID userId) {
        return this.userRepository.findById(userId);
    }

    @Override
    public User createUser(Jwt jwt) {
        User user = User.builder()
                .principalID(UUID.fromString(jwt.getSubject()))
                .name(jwt.getClaim("name"))
                .email(jwt.getClaim("email"))
                .roles(Role.NONE)
                .build();

        this.userRepository.save(user);

        return user;
    }

    @Override
    public void updateUser(UpdateUserRequest request) {
        User user = getUserById(request.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("User with ID " + request.getUserId() + " not found"));

        user.setRoles(request.getRoles());

        this.userRepository.save(user);
    }

    @Override
    public void updateUserRole(UUID userId, int role) {
        User user = getUserById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User with ID " + userId + " not found"));

        user.setRoles(Role.fromInt(role));

        this.userRepository.save(user);
    }

    @Override
    public void deleteUser(UUID userId) {
        if (!userExists(userId)) {
            throw new EntityNotFoundException("User with ID " + userId + " not found");
        }

        this.userRepository.deleteById(userId);
    }
}
