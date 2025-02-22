package com.mailmak.time_registration_system.test;

import com.mailmak.time_registration_system.classes.Role;
import com.mailmak.time_registration_system.classes.User;
import com.mailmak.time_registration_system.dto.users.UpdateUserRequest;
import com.mailmak.time_registration_system.exceptions.ForbiddenException;
import com.mailmak.time_registration_system.repository.UserRepository;
import com.mailmak.time_registration_system.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User testUser;
    private Jwt testJwt;
    private UUID testUserId;

    @BeforeEach
    public void setUp() {
        testUserId = UUID.randomUUID();
        testUser = User.builder()
                .principalID(testUserId)
                .name("Test User")
                .email("test@example.com")
                .roles(Role.EMPLOYEE)
                .build();

        testJwt = mock(Jwt.class);
    }

    @Test
    public void testUserExists_WhenUserPresent_ReturnsTrue() {
        when(userRepository.findById(testUserId)).thenReturn(Optional.of(testUser));

        boolean exists = userService.userExists(testUserId);

        assertTrue(exists);
        verify(userRepository).findById(testUserId);
    }

    @Test
    public void testUserExists_WhenUserNotPresent_ReturnsFalse() {
        when(userRepository.findById(testUserId)).thenReturn(Optional.empty());

        boolean exists = userService.userExists(testUserId);

        assertFalse(exists);
        verify(userRepository).findById(testUserId);
    }

    @Test
    public void testValidateRequiredRoles_WithCorrectRoles_Succeeds() {
        when(userRepository.findByPrincipalID(testUserId)).thenReturn(Optional.of(testUser));
        when(testJwt.getSubject()).thenReturn(testUserId.toString());

        assertDoesNotThrow(() -> userService.validateRequiredRoles(testJwt, Role.EMPLOYEE));
    }

    @Test
    public void testValidateRequiredRoles_WithIncorrectRoles_ThrowsForbiddenException() {
        testUser.setRoles(Role.MANAGER);
        when(testJwt.getSubject()).thenReturn(testUserId.toString());
        when(userRepository.findByPrincipalID(testUserId)).thenReturn(Optional.of(testUser));

        assertThrows(ForbiddenException.class, () ->
                userService.validateRequiredRoles(testJwt, Role.EMPLOYEE));
    }

    @Test
    public void testValidateRequiredRoles_UserNotFound_ThrowsEntityNotFoundException() {
        when(userRepository.findByPrincipalID(testUserId)).thenReturn(Optional.empty());
        when(testJwt.getSubject()).thenReturn(testUserId.toString());

        assertThrows(EntityNotFoundException.class, () ->
                userService.validateRequiredRoles(testJwt, Role.EMPLOYEE));
    }

    @Test
    public void testGetUsers_ReturnsListOfUsers() {
        List<User> expectedUsers = Arrays.asList(testUser,
                User.builder().principalID(UUID.randomUUID()).name("Another User").build());

        when(userRepository.findAll()).thenReturn(expectedUsers);

        List<User> actualUsers = userService.getUsers();

        assertEquals(expectedUsers, actualUsers);
        verify(userRepository).findAll();
    }

    @Test
    public void testGetAuthorizedUser_ExistingUser_ReturnsUser() {
        when(testJwt.getSubject()).thenReturn(testUserId.toString());
        when(userRepository.findByPrincipalID(testUserId)).thenReturn(Optional.of(testUser));

        User authorizedUser = userService.getAuthorizedUser(testJwt);

        assertEquals(testUser, authorizedUser);
    }

    @Test
    public void testGetAuthorizedUser_NewUser_CreatesAndReturnsUser() {
        when(userRepository.findByPrincipalID(testUserId)).thenReturn(Optional.empty());
        when(testJwt.getSubject()).thenReturn(testUserId.toString());
        when(testJwt.getClaim("name")).thenReturn("Test User");
        when(testJwt.getClaim("email")).thenReturn("test@example.com");

        User createdUser = userService.getAuthorizedUser(testJwt);

        assertNotNull(createdUser);
        assertEquals(testUserId, createdUser.getPrincipalID());
        assertEquals("Test User", createdUser.getName());
        verify(userRepository).save(any(User.class));
    }

    @Test
    public void testGetUserById_ExistingUser_ReturnsUser() {
        when(userRepository.findById(testUserId)).thenReturn(Optional.of(testUser));

        Optional<User> foundUser = userService.getUserById(testUserId);

        assertTrue(foundUser.isPresent());
        assertEquals(testUser, foundUser.get());
    }

    @Test
    public void testUpdateUser_ValidRequest_UpdatesUser() {
        when(userRepository.findById(testUserId)).thenReturn(Optional.of(testUser));

        UpdateUserRequest updateRequest = new UpdateUserRequest();
        updateRequest.setUserId(testUserId);
        updateRequest.setRoles(Role.MANAGER);

        userService.updateUser(updateRequest);

        assertEquals(Role.MANAGER, testUser.getRoles());
        verify(userRepository).save(testUser);
    }

    @Test
    public void testUpdateUser_NonExistentUser_ThrowsEntityNotFoundException() {
        when(userRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

        UpdateUserRequest updateRequest = new UpdateUserRequest();
        updateRequest.setUserId(testUserId);
        updateRequest.setRoles(Role.MANAGER);

        assertThrows(EntityNotFoundException.class, () -> userService.updateUser(updateRequest));
    }

    @Test
    public void testDeleteUser_ExistingUser_DeletesUser() {
        when(userRepository.findById(testUserId)).thenReturn(Optional.of(testUser));

        userService.deleteUser(testUserId);

        verify(userRepository).deleteById(testUserId);
    }

    @Test
    public void testDeleteUser_NonExistentUser_ThrowsEntityNotFoundException() {
        when(userRepository.findById(testUserId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> userService.deleteUser(testUserId));
    }
}