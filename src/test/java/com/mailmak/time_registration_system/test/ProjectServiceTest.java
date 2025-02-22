package com.mailmak.time_registration_system.test;

import com.mailmak.time_registration_system.classes.Customer;
import com.mailmak.time_registration_system.classes.Project;
import com.mailmak.time_registration_system.dto.projects.CreateProjectRequest;
import com.mailmak.time_registration_system.dto.projects.GetProjectRequest;
import com.mailmak.time_registration_system.dto.projects.UpdateProjectRequest;
import com.mailmak.time_registration_system.repository.CustomerRepository;
import com.mailmak.time_registration_system.repository.ProjectRepository;
import com.mailmak.time_registration_system.service.ProjectService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {
    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private ProjectService projectService;

    @Test
    void getProject_WhenProjectExists_ShouldReturnProject() {
        // ARRANGE
        UUID projectId = UUID.randomUUID();
        Project expectedProject = new Project();
        expectedProject.setId(projectId);

        // Mock the repository to return an Optional containing the project
        when(projectRepository.findById(projectId))
                .thenReturn(Optional.of(expectedProject));

        // ACT
        Project actualProject = projectService.getProject(projectId);

        // ASSERT
        assertNotNull(actualProject);
        assertEquals(expectedProject, actualProject);
        // Verify interactions
        verify(projectRepository).findById(projectId);
    }

    @Test
    void getProject_WhenProjectDoesNotExist_ShouldThrowEntityNotFoundException() {
        // ARRANGE
        UUID nonExistentProjectId = UUID.randomUUID();

        // Mock the repository to return an empty Optional
        when(projectRepository.findById(nonExistentProjectId))
                .thenReturn(Optional.empty());

        // ACT and ASSERT
        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> projectService.getProject(nonExistentProjectId)
        );

        assertEquals(
                "Project with ID: " + nonExistentProjectId + " cannot be found",
                exception.getMessage()
        );
        // Verify interactions
        verify(projectRepository).findById(nonExistentProjectId);
    }

    @Test
    void getProjects_WhenNoCustomerIdProvided_ShouldReturnAllProjects() {
        // ARRANGE
        GetProjectRequest request = new GetProjectRequest();
        request.setCustomerId(null);

        List<Project> expectedProjects = new ArrayList<>();
        expectedProjects.add(new Project());
        expectedProjects.add(new Project());
        expectedProjects.add(new Project());

        // Mock the repository to return all projects
        when(projectRepository.findAll())
                .thenReturn(expectedProjects);

        // ACT
        ArrayList<Project> actualProjects = projectService.getProjects(request);

        // ASSERT
        assertNotNull(actualProjects);
        assertEquals(expectedProjects.size(), actualProjects.size());
        // Verify interactions
        verify(projectRepository).findAll();
        verifyNoInteractions(customerRepository);
    }

    @Test
    void getProjects_WhenValidCustomerIdProvided_ShouldReturnProjectsForCustomer() {
        // ARRANGE
        UUID customerId = UUID.randomUUID();
        GetProjectRequest request = new GetProjectRequest();
        request.setCustomerId(customerId);

        Customer customer = new Customer();
        customer.setId(customerId);

        ArrayList<Project> expectedProjects = new ArrayList<>();
        expectedProjects.add(new Project());
        expectedProjects.add(new Project());
        expectedProjects.add(new Project());

        // Mock the customer repository to find the customer
        when(customerRepository.findById(customerId))
                .thenReturn(Optional.of(customer));

        // Mock the project repository to find projects for the customer
        when(projectRepository.findByCustomer(customer))
                .thenReturn(expectedProjects);

        // ACT
        ArrayList<Project> actualProjects = projectService.getProjects(request);

        // ASSERT
        assertNotNull(actualProjects);
        assertEquals(expectedProjects.size(), actualProjects.size());
        // Verify interactions
        verify(customerRepository).findById(customerId);
        verify(projectRepository).findByCustomer(customer);
    }

    @Test
    void getProjects_WhenNonExistentCustomerIdProvided_ShouldThrowEntityNotFoundException() {
        // ARRANGE
        UUID nonExistentCustomerId = UUID.randomUUID();
        GetProjectRequest request = new GetProjectRequest();
        request.setCustomerId(nonExistentCustomerId);

        // Mock the customer repository to return empty optional
        when(customerRepository.findById(nonExistentCustomerId))
                .thenReturn(Optional.empty());

        // ACT and ASSERT
        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> projectService.getProjects(request)
        );

        assertEquals(
                "Customer with ID: " + nonExistentCustomerId + " cannot be found",
                exception.getMessage()
        );

        // Verify interactions
        verify(customerRepository).findById(nonExistentCustomerId);
        verifyNoInteractions(projectRepository);
    }


    @Test
    void test_CreateProject_WhenCustomerExists() {
        // ARRANGE
        UUID customerId = UUID.randomUUID();
        Customer customer = new Customer();
        customer.setId(customerId);

        CreateProjectRequest request = new CreateProjectRequest();
        request.setName("Test Project");
        request.setDescription("Test Description");
        request.setCustomerId(customerId);

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(customer));

        Project savedProject = new Project();
        savedProject.setName(request.getName());
        savedProject.setDescription(request.getDescription());
        savedProject.setCustomer(customer);

        when(projectRepository.save(any(Project.class))).thenReturn(savedProject);

        // ACT
        Project createdProject = projectService.createProject(request);

        // ASSERT
        assertNotNull(createdProject, "Created project should not be null");
        assertEquals(request.getName(), createdProject.getName(), "Project name should match request");
        assertEquals(request.getDescription(), createdProject.getDescription(), "Project description should match request");
        assertEquals(customer, createdProject.getCustomer(), "Project customer should match input customer");

        // Verify interactions
        verify(customerRepository).findById(customerId);
        verify(projectRepository).save(any(Project.class));
    }

    @Test
    void test_CreateProject_WhenCustomerNotFound() {
        // ARRANGE
        UUID customerId = UUID.randomUUID();

        CreateProjectRequest request = new CreateProjectRequest();
        request.setName("Test Project");
        request.setDescription("Test Description");
        request.setCustomerId(customerId);

        when(customerRepository.findById(customerId)).thenReturn(Optional.empty());

        // ACT and ASSERT
        assertThrows(EntityNotFoundException.class,
                () -> projectService.createProject(request),
                "Should throw EntityNotFoundException when customer not found");
    }

    @Test
    void Test_updateProject_WithValidData() {
        // ARRANGE
        UUID projectId = UUID.randomUUID();
        UUID newCustomerId = UUID.randomUUID();

        Project existingProject = new Project();
        existingProject.setId(projectId);
        existingProject.setName("Old Project Name");
        existingProject.setDescription("Old Description");

        Customer newCustomer = new Customer();
        newCustomer.setId(newCustomerId);

        UpdateProjectRequest request = new UpdateProjectRequest();
        request.setName("New Project Name");
        request.setDescription("New Description");
        request.setCustomerId(newCustomerId);

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(existingProject));
        when(customerRepository.findById(newCustomerId)).thenReturn(Optional.of(newCustomer));

        // ACT
        projectService.updateProject(request, projectId);

        // ASSERT
        assertEquals("New Project Name", existingProject.getName(), "Project name should be updated");
        assertEquals("New Description", existingProject.getDescription(), "Project description should be updated");
        assertEquals(newCustomer, existingProject.getCustomer(), "Project customer should be updated");

        // Verify interactions
        verify(projectRepository).findById(projectId);
        verify(customerRepository).findById(newCustomerId);
        verify(projectRepository).save(existingProject);
    }

    @Test
    @DisplayName("Should throw exception when new customer not found")
    void updateProject_WhenNewCustomerNotFound_ThrowsException() {
        // ARRANGE
        UUID projectId = UUID.randomUUID();
        UUID nonExistentCustomerId = UUID.randomUUID();

        Project existingProject = new Project();
        existingProject.setId(projectId);
        existingProject.setName("Old Project Name");

        UpdateProjectRequest request = new UpdateProjectRequest();
        request.setCustomerId(nonExistentCustomerId);

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(existingProject));
        when(customerRepository.findById(nonExistentCustomerId)).thenReturn(Optional.empty());

        // ACT and ASSERT
        assertThrows(EntityNotFoundException.class,
                () -> projectService.updateProject(request, projectId),
                "Should throw EntityNotFoundException when customer does not exist");

        // Verify interactions
        verify(projectRepository).findById(projectId);
        verify(customerRepository).findById(nonExistentCustomerId);
        verify(projectRepository, never()).save(any(Project.class));
    }

    @Test
    @DisplayName("Should delete project when it exists")
    void deleteProject_WhenProjectExists_DeletesSuccessfully() {
        // ARRANGE
        UUID projectId = UUID.randomUUID();

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(new Project()));

        // ACT
        projectService.deleteProject(projectId);

        // ASSERT (void - but verify the repository interactions
        verify(projectRepository).findById(projectId);
        verify(projectRepository).deleteById(projectId);
    }

    @Test
    @DisplayName("Should throw exception when project does not exist")
    void deleteProject_WhenProjectDoesNotExist_ThrowsException() {
        // ARRANGE
        UUID projectId = UUID.randomUUID();

        when(projectRepository.findById(projectId)).thenReturn(Optional.empty());

        // ACT and ASSERT
        assertThrows(RuntimeException.class,
                () -> projectService.deleteProject(projectId),
                "Should throw RuntimeException when project does not exist");

        // Verify repository interactions
        verify(projectRepository).findById(projectId);
        verify(projectRepository, never()).deleteById(projectId);
    }
}