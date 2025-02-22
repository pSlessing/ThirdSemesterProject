package com.mailmak.time_registration_system.test;

import com.mailmak.time_registration_system.classes.Project;
import com.mailmak.time_registration_system.classes.Role;
import com.mailmak.time_registration_system.controller.ProjectController;
import com.mailmak.time_registration_system.dto.projects.CreateProjectRequest;
import com.mailmak.time_registration_system.dto.projects.GetProjectRequest;
import com.mailmak.time_registration_system.dto.projects.ProjectResponse;
import com.mailmak.time_registration_system.dto.projects.UpdateProjectRequest;
import com.mailmak.time_registration_system.exceptions.ForbiddenException;
import com.mailmak.time_registration_system.mappers.ProjectResponseMapper;
import com.mailmak.time_registration_system.service.ProjectServiceInterface;
import com.mailmak.time_registration_system.service.UserServiceInterface;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProjectControllerTest {
    @Mock
    private ProjectServiceInterface projectService;

    @Mock
    private UserServiceInterface userService;

    @Mock
    private ProjectResponseMapper projectResponseMapper;

    @InjectMocks
    private ProjectController projectController;

    @Mock
    private Jwt jwt;

    //Test cases for getAllProjects//
    @Test
    void getAllProjects_WhenManagerRequestsAllProjects_ShouldReturnOkResponse() {
        // ARRANGE
        UUID customerId = null;

        // Create sample projects
        ArrayList<Project> projects = new ArrayList<Project>();
        projects.add(Project.builder()
                        .id(UUID.randomUUID())
                        .name("Project 1")
                        .description("Description 1")
                        .build());
        projects.add(Project.builder()
                        .id(UUID.randomUUID())
                        .name("Project 2")
                        .description("Description 2")
                        .build()
        );

        // Create corresponding project responses
        ArrayList<ProjectResponse> projectResponses = new ArrayList<ProjectResponse>();
        projectResponses.add(
                ProjectResponse.builder()
                        .id(projects.get(0).getId())
                        .name(projects.get(0).getName())
                        .description(projects.get(0).getDescription())
                        .build());
        projectResponses.add(
                ProjectResponse.builder()
                        .id(projects.get(1).getId())
                        .name(projects.get(1).getName())
                        .description(projects.get(1).getDescription())
                        .build()
        );

        // Mock user service validation (for manager role)
        doNothing().when(userService).validateRequiredRoles(jwt, Role.EMPLOYEE, Role.MANAGER);

        // Mock project service to return projects
        GetProjectRequest expectedRequest = new GetProjectRequest();
        when(projectService.getProjects(expectedRequest)).thenReturn(projects);

        // Mock mapper to convert projects to responses
        when(projectResponseMapper.mapTo(projects.get(0))).thenReturn(projectResponses.get(0));
        when(projectResponseMapper.mapTo(projects.get(1))).thenReturn(projectResponses.get(1));

        // ACT
        ResponseEntity<List<ProjectResponse>> response = projectController.getAllProjects(jwt, customerId);

        // ASSERT
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        assertEquals(projects.get(0).getId(), response.getBody().get(0).getId());
        assertEquals(projects.get(0).getName(), response.getBody().get(0).getName());
        assertEquals(projects.get(0).getDescription(), response.getBody().get(0).getDescription());

        // Verify interactions
        verify(userService).validateRequiredRoles(jwt, Role.EMPLOYEE, Role.MANAGER);
        verify(projectService).getProjects(any(GetProjectRequest.class));
        verify(projectResponseMapper, times(2)).mapTo(any(Project.class));
    }

    @Test
    void getAllProjects_WhenManagerRequestsProjectsForSpecificCustomer_ShouldReturnOkResponse() {
        // ARRANGE
        UUID customerId = UUID.randomUUID();

        // Create sample projects
        ArrayList<Project> projects = new ArrayList<Project>();
        projects.add(Project.builder().build());
        projects.add(Project.builder().build());

        // Create corresponding project responses
        ArrayList<ProjectResponse> projectResponses = new ArrayList<ProjectResponse>();
        projectResponses.add(ProjectResponse.builder().build());
        projectResponses.add(ProjectResponse.builder().build());

        // Mock user service validation (for manager role)
        doNothing().when(userService).validateRequiredRoles(jwt, Role.EMPLOYEE, Role.MANAGER);

        // Mock project service to return projects
        GetProjectRequest expectedRequest = new GetProjectRequest();
        expectedRequest.setCustomerId(customerId);
        when(projectService.getProjects(expectedRequest)).thenReturn(projects);

        // Mock mapper to convert projects to responses
        when(projectResponseMapper.mapTo(projects.get(0))).thenReturn(projectResponses.get(0));
        when(projectResponseMapper.mapTo(projects.get(1))).thenReturn(projectResponses.get(1));

        // ACT
        ResponseEntity<List<ProjectResponse>> response = projectController.getAllProjects(jwt, customerId);

        // ASSERT
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());

        // Verify interactions
        verify(userService).validateRequiredRoles(jwt, Role.EMPLOYEE, Role.MANAGER);
        verify(projectService).getProjects(any(GetProjectRequest.class));
        verify(projectResponseMapper, times(2)).mapTo(any(Project.class));
    }

    @Test
    void getAllProjects_WhenForbiddenExceptionThrown_ShouldReturnForbiddenStatus() {
        // Arrange
        UUID customerId = null;

        // Mock user service to throw ForbiddenException
        doThrow(new ForbiddenException("Insufficient permissions"))
                .when(userService).validateRequiredRoles(jwt, Role.EMPLOYEE, Role.MANAGER);

        // ACT
        ResponseEntity<List<ProjectResponse>> response = projectController.getAllProjects(jwt, customerId);

        // ASSERT
        assertNotNull(response);
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertNull(response.getBody());

        // Verify interactions
        verify(userService).validateRequiredRoles(jwt, Role.EMPLOYEE, Role.MANAGER);
        verifyNoInteractions(projectService);
        verifyNoInteractions(projectResponseMapper);
    }

    @Test
    void getAllProjects_WhenUnexpectedExceptionThrown_ShouldReturnInternalServerError() {
        // ARRANGE
        UUID customerId = null;

        // Mock user service validation (for manager role)
        doNothing().when(userService).validateRequiredRoles(jwt, Role.EMPLOYEE, Role.MANAGER);

        // Mock project service to throw an unexpected exception
        when(projectService.getProjects(any(GetProjectRequest.class)))
                .thenThrow(new RuntimeException("Unexpected error"));

        // ACT
        ResponseEntity<List<ProjectResponse>> response = projectController.getAllProjects(jwt, customerId);

        // ASSERT
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNull(response.getBody());

        // Verify interactions
        verify(userService).validateRequiredRoles(jwt, Role.EMPLOYEE, Role.MANAGER);
        verify(projectService).getProjects(any(GetProjectRequest.class));
        verifyNoInteractions(projectResponseMapper);
    }

    @Test
    void getProjectById_WhenValidProjectAndAuthorizedUser_ShouldReturnOkResponse() {
        // ARRANGE
        UUID projectId = UUID.randomUUID();

        // Create a sample project
        Project project = Project.builder()
                .id(projectId)
                .name("Test Project")
                .description("Project Description")
                .build();

        // Create corresponding project response
        ProjectResponse projectResponse = ProjectResponse.builder()
                .id(projectId)
                .name("Test Project")
                .description("Project Description")
                .build();

        // Mock user service validation
        doNothing().when(userService).validateRequiredRoles(jwt, Role.EMPLOYEE, Role.MANAGER);

        // Mock project service to return project
        when(projectService.getProject(projectId)).thenReturn(project);

        // Mock mapper to convert project to response
        when(projectResponseMapper.mapTo(project)).thenReturn(projectResponse);

        // Act
        ResponseEntity<ProjectResponse> response = projectController.getProjectById(jwt, projectId);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(projectId, response.getBody().getId());
        assertEquals("Test Project", response.getBody().getName());
        assertEquals("Project Description", response.getBody().getDescription());

        // Verify interactions
        verify(userService).validateRequiredRoles(jwt, Role.EMPLOYEE, Role.MANAGER);
        verify(projectService).getProject(projectId);
        verify(projectResponseMapper).mapTo(project);
    }

    @Test
    void getProjectById_WhenForbiddenExceptionThrown_ShouldReturnForbiddenStatus() {
        // ARRANGE
        UUID projectId = UUID.randomUUID();

        // Mock user service to throw ForbiddenException
        doThrow(new ForbiddenException("Insufficient permissions"))
                .when(userService).validateRequiredRoles(jwt, Role.EMPLOYEE, Role.MANAGER);

        // ACT
        ResponseEntity<ProjectResponse> response = projectController.getProjectById(jwt, projectId);

        // ASSERT
        assertNotNull(response);
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertNull(response.getBody());

        // Verify interactions
        verify(userService).validateRequiredRoles(jwt, Role.EMPLOYEE, Role.MANAGER);
        verifyNoInteractions(projectService);
        verifyNoInteractions(projectResponseMapper);
    }

    @Test
    void getProjectById_WhenProjectNotFound_ShouldReturnNotFoundStatus() {
        // ARRANGE
        UUID projectId = UUID.randomUUID();

        // Mock user service validation
        doNothing().when(userService).validateRequiredRoles(jwt, Role.EMPLOYEE, Role.MANAGER);

        // Mock project service to throw EntityNotFoundException
        when(projectService.getProject(projectId))
                .thenThrow(new EntityNotFoundException("Project not found"));

        // ACT
        ResponseEntity<ProjectResponse> response = projectController.getProjectById(jwt, projectId);

        // ASSERT
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());

        // Verify interactions
        verify(userService).validateRequiredRoles(jwt, Role.EMPLOYEE, Role.MANAGER);
        verify(projectService).getProject(projectId);
        verifyNoInteractions(projectResponseMapper);
    }

    @Test
    void getProjectById_WhenUnexpectedExceptionThrown_ShouldReturnInternalServerError() {
        // ARRANGE
        UUID projectId = UUID.randomUUID();

        // Mock user service validation
        doNothing().when(userService).validateRequiredRoles(jwt, Role.EMPLOYEE, Role.MANAGER);

        // Mock project service to throw an unexpected exception
        when(projectService.getProject(projectId))
                .thenThrow(new RuntimeException("Unexpected error"));

        // ACT
        ResponseEntity<ProjectResponse> response = projectController.getProjectById(jwt, projectId);

        // ASSERT
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNull(response.getBody());

        // Verify interactions
        verify(userService).validateRequiredRoles(jwt, Role.EMPLOYEE, Role.MANAGER);
        verify(projectService).getProject(projectId);
        verifyNoInteractions(projectResponseMapper);
    }

    @Test
    void createProject_WhenValidRequestAndAuthorizedUser_ShouldReturnCreatedProject() {
        // ARANGE
        CreateProjectRequest createRequest = new CreateProjectRequest();
        createRequest.setName("New Project");
        createRequest.setDescription("Project Description");
        createRequest.setCustomerId(UUID.randomUUID());

        // Create a sample project from the request
        Project createdProject = Project.builder()
                .id(UUID.randomUUID())
                .name(createRequest.getName())
                .description(createRequest.getDescription())
                .build();

        // Create corresponding project response
        ProjectResponse projectResponse = ProjectResponse.builder()
                .id(createdProject.getId())
                .name(createdProject.getName())
                .description(createdProject.getDescription())
                .build();

        // Mock user service validation
        doNothing().when(userService).validateRequiredRoles(jwt, Role.MANAGER);

        // Mock project service to create project
        when(projectService.createProject(createRequest)).thenReturn(createdProject);

        // Mock mapper to convert project to response
        when(projectResponseMapper.mapTo(createdProject)).thenReturn(projectResponse);

        // ACT
        ResponseEntity<ProjectResponse> response = projectController.createProject(createRequest, jwt);

        // ASSERT
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(createdProject.getId(), response.getBody().getId());
        assertEquals(createRequest.getName(), response.getBody().getName());
        assertEquals(createRequest.getDescription(), response.getBody().getDescription());

        // Verify interactions
        verify(userService).validateRequiredRoles(jwt, Role.MANAGER);
        verify(projectService).createProject(createRequest);
        verify(projectResponseMapper).mapTo(createdProject);
    }

    @Test
    void createProject_WhenForbiddenExceptionThrown_ShouldReturnForbiddenStatus() {
        // ARRANGE
        CreateProjectRequest createRequest = new CreateProjectRequest();
        createRequest.setName("New Project");
        createRequest.setDescription("Project Description");
        createRequest.setCustomerId(UUID.randomUUID());

        // Mock user service to throw ForbiddenException
        doThrow(new ForbiddenException("Insufficient permissions"))
                .when(userService).validateRequiredRoles(jwt, Role.MANAGER);

        // ACT
        ResponseEntity<ProjectResponse> response = projectController.createProject(createRequest, jwt);

        // ASSERT
        assertNotNull(response);
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertNull(response.getBody());

        // Verify interactions
        verify(userService).validateRequiredRoles(jwt, Role.MANAGER);
        verifyNoInteractions(projectService);
        verifyNoInteractions(projectResponseMapper);
    }

    @Test
    void createProject_WhenUnexpectedExceptionThrown_ShouldReturnBadRequest() {
        // ARRANGE
        CreateProjectRequest createRequest = new CreateProjectRequest();
        createRequest.setName("New Project");
        createRequest.setDescription("Project Description");
        createRequest.setCustomerId(UUID.randomUUID());

        // Mock user service validation
        doNothing().when(userService).validateRequiredRoles(jwt, Role.MANAGER);

        // Mock project service to throw an unexpected exception
        when(projectService.createProject(createRequest))
                .thenThrow(new RuntimeException("Unexpected error"));

        // ACT
        ResponseEntity<ProjectResponse> response = projectController.createProject(createRequest, jwt);

        // ASSERT
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNull(response.getBody());

        // Verify interactions
        verify(userService).validateRequiredRoles(jwt, Role.MANAGER);
        verify(projectService).createProject(createRequest);
        verifyNoInteractions(projectResponseMapper);
    }

    @Test
    void updateProject_WhenValidRequestAndAuthorizedUser_ShouldReturnNoContent() {
        // ARRANGE
        UUID projectId = UUID.randomUUID();
        UpdateProjectRequest updateRequest = new UpdateProjectRequest();
        updateRequest.setName("Updated Project Name");
        updateRequest.setDescription("Updated Project Description");

        // Mock user service validation
        doNothing().when(userService).validateRequiredRoles(jwt, Role.MANAGER);

        // Mock project service update method
        doNothing().when(projectService).updateProject(updateRequest, projectId);

        // ACT
        ResponseEntity<Void> response = projectController.updateProject(jwt, projectId, updateRequest);

        // ASSERT
        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());

        // Verify interactions
        verify(userService).validateRequiredRoles(jwt, Role.MANAGER);
        verify(projectService).updateProject(updateRequest, projectId);
    }

    @Test
    void updateProject_WhenForbiddenExceptionThrown_ShouldReturnForbiddenStatus() {
        // ARRANGE
        UUID projectId = UUID.randomUUID();
        UpdateProjectRequest updateRequest = new UpdateProjectRequest();
        updateRequest.setName("Updated Project Name");
        updateRequest.setDescription("Updated Project Description");

        // Mock user service to throw ForbiddenException
        doThrow(new ForbiddenException("Insufficient permissions"))
                .when(userService).validateRequiredRoles(jwt, Role.MANAGER);

        // ACT
        ResponseEntity<Void> response = projectController.updateProject(jwt, projectId, updateRequest);

        // ASSERT
        assertNotNull(response);
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertNull(response.getBody());

        // Verify interactions
        verify(userService).validateRequiredRoles(jwt, Role.MANAGER);
        verifyNoInteractions(projectService);
    }

    @Test
    void updateProject_WhenProjectNotFound_ShouldReturnNotFoundStatus() {
        // ARRANGE
        UUID projectId = UUID.randomUUID();
        UpdateProjectRequest updateRequest = new UpdateProjectRequest();
        updateRequest.setName("Updated Project Name");
        updateRequest.setDescription("Updated Project Description");

        // Mock user service validation
        doNothing().when(userService).validateRequiredRoles(jwt, Role.MANAGER);

        // Mock project service to throw EntityNotFoundException
        doThrow(new EntityNotFoundException("Project not found"))
                .when(projectService).updateProject(updateRequest, projectId);

        // ACT
        ResponseEntity<Void> response = projectController.updateProject(jwt, projectId, updateRequest);

        // ASSERT
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());

        // Verify interactions
        verify(userService).validateRequiredRoles(jwt, Role.MANAGER);
        verify(projectService).updateProject(updateRequest, projectId);
    }

    @Test
    void updateProject_WhenInvalidArguments_ShouldReturnBadRequestStatus() {
        // ARRANGE
        UUID projectId = UUID.randomUUID();
        UpdateProjectRequest updateRequest = new UpdateProjectRequest();
        updateRequest.setName("Updated Project Name");
        updateRequest.setDescription("Updated Project Description");

        // Mock user service validation
        doNothing().when(userService).validateRequiredRoles(jwt, Role.MANAGER);

        // Mock project service to throw IllegalArgumentException
        doThrow(new IllegalArgumentException("Invalid parameters"))
                .when(projectService).updateProject(updateRequest, projectId);

        // ACT
        ResponseEntity<Void> response = projectController.updateProject(jwt, projectId, updateRequest);

        // ASSERT
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNull(response.getBody());

        // Verify interactions
        verify(userService).validateRequiredRoles(jwt, Role.MANAGER);
        verify(projectService).updateProject(updateRequest, projectId);
    }

    @Test
    void updateProject_WhenUnexpectedExceptionThrown_ShouldReturnInternalServerError() {
        // ARRANGE
        UUID projectId = UUID.randomUUID();
        UpdateProjectRequest updateRequest = new UpdateProjectRequest();
        updateRequest.setName("Updated Project Name");
        updateRequest.setDescription("Updated Project Description");
        updateRequest.setCustomerId(UUID.randomUUID());

        // Mock user service validation
        doNothing().when(userService).validateRequiredRoles(jwt, Role.MANAGER);

        // Mock project service to throw an unexpected exception
        doThrow(new RuntimeException("Unexpected error"))
                .when(projectService).updateProject(updateRequest, projectId);

        // ACT
        ResponseEntity<Void> response = projectController.updateProject(jwt, projectId, updateRequest);

        // ASSERT
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNull(response.getBody());

        // Verify interactions
        verify(userService).validateRequiredRoles(jwt, Role.MANAGER);
        verify(projectService).updateProject(updateRequest, projectId);
    }

    @Test
    void deleteProject_WhenUserLacksPermission_ShouldReturnForbiddenError() {
        // ARRANGE
        UUID projectId = UUID.randomUUID();

        // Mock user service to throw ForbiddenException
        doThrow(new ForbiddenException("Insufficient permissions"))
                .when(userService).validateRequiredRoles(jwt, Role.MANAGER);

        // ACT
        ResponseEntity<Void> response = projectController.deleteProject(jwt, projectId);

        // ASSERT
        assertNotNull(response);
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertNull(response.getBody());

        // Verify interaction
        verify(userService).validateRequiredRoles(jwt, Role.MANAGER);
        verifyNoInteractions(projectService);
    }

    @Test
    void deleteProject_WhenProjectNotFound_ShouldReturnNotFoundError() {
        // ARRANGE
        UUID projectId = UUID.randomUUID();

        // Mock user service validation
        doNothing().when(userService).validateRequiredRoles(jwt, Role.MANAGER);

        // Mock project service to throw EntityNotFoundException
        doThrow(new EntityNotFoundException("Project not found"))
                .when(projectService).deleteProject(projectId);

        // ACT
        ResponseEntity<Void> response = projectController.deleteProject(jwt, projectId);

        // ASSERT
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());

        // Verify interactions
        verify(userService).validateRequiredRoles(jwt, Role.MANAGER);
        verify(projectService).deleteProject(projectId);
    }

    @Test
    void deleteProject_WhenProjectDeletedSuccessfully_ShouldReturnNoContent() {
        // ARRANGE
        UUID projectId = UUID.randomUUID();

        // Mock user service validation
        doNothing().when(userService).validateRequiredRoles(jwt, Role.MANAGER);

        // ACT
        ResponseEntity<Void> response = projectController.deleteProject(jwt, projectId);

        // ASSERT
        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());

        // Verify interactions
        verify(userService).validateRequiredRoles(jwt, Role.MANAGER);
        verify(projectService).deleteProject(projectId);
    }

    @Test
    void deleteProject_WhenUnexpectedExceptionThrown_ShouldReturnInternalServerError() {
        // ARRANGE
        UUID projectId = UUID.randomUUID();

        // Mock user service validation
        doNothing().when(userService).validateRequiredRoles(jwt, Role.MANAGER);

        // Mock project service to throw an unexpected exception
        doThrow(new RuntimeException("Unexpected error"))
                .when(projectService).deleteProject(projectId);

        // ACT
        ResponseEntity<Void> response = projectController.deleteProject(jwt, projectId);

        // ASSERT
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNull(response.getBody());

        // Verify interactions
        verify(userService).validateRequiredRoles(jwt, Role.MANAGER);
        verify(projectService).deleteProject(projectId);
    }
}

