package com.mailmak.time_registration_system.test.services;

import com.mailmak.time_registration_system.TimeRegistrationSystemApplicationTests;
import com.mailmak.time_registration_system.classes.Customer;
import com.mailmak.time_registration_system.classes.Project;
import com.mailmak.time_registration_system.dto.projects.CreateProjectRequest;
import com.mailmak.time_registration_system.dto.projects.GetProjectRequest;
import com.mailmak.time_registration_system.dto.projects.UpdateProjectRequest;
import com.mailmak.time_registration_system.repository.CustomerRepository;
import com.mailmak.time_registration_system.repository.ProjectRepository;
import com.mailmak.time_registration_system.service.ProjectService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.UUID;

import com.mailmak.time_registration_system.service.ProjectServiceInterface;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;

/** Integration test for {@link ProjectService} */

@SpringBootTest
@ExtendWith(SpringExtension.class)
@Import(TimeRegistrationSystemApplicationTests.TestSecurityConfig.class)
public class ProjectServiceIT {
    @Autowired
    private ProjectServiceInterface projectService;
    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private CustomerRepository customerRepository;

    private UUID firstCustomerId;
    private UUID firstProjectId;
    private String firstDummyProjectName;

    @BeforeEach
    public void setUp() {
        // Clear DB.
        projectRepository.deleteAll();
        customerRepository.deleteAll();

        Customer firstDummyCustomer = new Customer();
        firstDummyCustomer.setName("Dummy Customer #1");
        Customer savedCustomer = customerRepository.save(firstDummyCustomer);
        firstCustomerId = savedCustomer.getId();

        firstDummyProjectName = "Dummy Project #1";

        Project firstDummyProject = new Project();
        firstDummyProject.setName(firstDummyProjectName);
        firstDummyProject.setDescription("Dummy Project #1 Description");
        firstDummyProject.setCustomer(firstDummyCustomer);
        Project savedProject = projectRepository.saveAndFlush(firstDummyProject);
        firstProjectId = savedProject.getId();

        Project secondDummyProject = new Project();
        secondDummyProject.setId(UUID.randomUUID());
        secondDummyProject.setName("Dummy Project #2");
        secondDummyProject.setDescription("Dummy Project #2 Description");
        secondDummyProject.setCustomer(firstDummyCustomer);
        projectRepository.saveAndFlush(secondDummyProject);

        Project thirdDummyProject = new Project();
        thirdDummyProject.setId(UUID.randomUUID());
        thirdDummyProject.setName("Dummy Project #3");
        thirdDummyProject.setDescription("Dummy Project #3 Description");
        thirdDummyProject.setCustomer(firstDummyCustomer);
        projectRepository.saveAndFlush(thirdDummyProject);
    }

    @Test
    public void testGetProjects_WithCustomerId_ReturnsCorrectProjects() {
        GetProjectRequest request = new GetProjectRequest();
        request.setCustomerId(firstCustomerId);
        final ArrayList<Project> result = projectService.getProjects(request);
        assertThat(result).isNotNull(); // Ensure the result is not null.
        assertThat(result).isNotEmpty(); // Ensure the result is not empty.
        assertThat(result.size()).isEqualTo(3); // Ensure the result contains three projects.
    }

    @Test
    public void testGetProject_WithProjectId_ReturnsCorrectProject() {
        final Project result = projectService.getProject(firstProjectId);
        assertThat(result).isNotNull(); // Ensure the project exists.
        assertThat(result.getName()).isEqualTo(firstDummyProjectName); // Check project name.
        assertThat(result.getCustomer().getId()).isEqualTo(firstCustomerId); // Check customer ID.
    }

    @Test
    public void testCreateProject_WithValidRequest_CreatesAndReturnsProject() {
        CreateProjectRequest request = new CreateProjectRequest();
        request.setCustomerId(firstCustomerId);
        request.setName("Test project #1");
        request.setDescription("Test project description #1");

        final Project resultCreated = projectService.createProject(request); // Create project.
        final Project result = projectService.getProject(resultCreated.getId()); // Access project in the DB.

        assertThat(result).isNotNull(); // Ensure the project exists in the DB.
        assertThat(result.getName()).isEqualTo("Test project #1"); // Check project name.
        assertThat(result.getDescription()).isEqualTo("Test project description #1"); // Check project description.
    }

    @Test
    public void testUpdateProject_WithValidRequest_UpdatesProject() {
        String newProjectName = "New dummy project name";
        String newProjectDescription = "New dummy project description";

        UpdateProjectRequest request = new UpdateProjectRequest();
        request.setName(newProjectName);
        request.setDescription(newProjectDescription);
        request.setCustomerId(firstCustomerId);

        projectService.updateProject(request, firstProjectId);

        // Grab updated project in the DB and make sure it has the correct properties.
        final Project result = projectService.getProject(firstProjectId);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo(newProjectName);
        assertThat(result.getDescription()).isEqualTo(newProjectDescription);
        assertThat(result.getCustomer().getId()).isEqualTo(firstCustomerId);
    }

    @Test
    public void testDeleteProject_WithProjectId_DeletesProject() {
        // Ensure the project exists before deletion.
        final Project result = projectService.getProject(firstProjectId);
        assertThat(result).isNotNull(); 

        projectService.deleteProject(firstProjectId); // Delete the project.

        // Check that GetProject with the deleted ID throws an EntityNotFoundException.
        assertThrows(EntityNotFoundException.class, () -> {
            projectService.getProject(firstProjectId);
        });
    }
}