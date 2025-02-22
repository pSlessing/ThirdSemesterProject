package com.mailmak.time_registration_system.controller;
import com.mailmak.time_registration_system.classes.Project;
import com.mailmak.time_registration_system.classes.Role;
import com.mailmak.time_registration_system.exceptions.ForbiddenException;
import com.mailmak.time_registration_system.mappers.ProjectResponseMapper;
import com.mailmak.time_registration_system.dto.projects.*;

import com.mailmak.time_registration_system.service.ProjectServiceInterface;
import com.mailmak.time_registration_system.service.UserServiceInterface;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.web.bind.annotation.*;  //all annotations and classes to define the REST API
import org.springframework.web.bind.annotation.GetMapping;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.List;
import java.util.UUID;


@RestController         //Marking this class as a REST controller in Spring Boot
@RequestMapping("/api/projects") //we are mapping all endpoints of this controller to a URL path that starts like this.

@Tag(name = "Projects", description = "Endpoint for managing projects")
public class ProjectController {

    private final ProjectServiceInterface projectService;                     //Defining dependency: This service handles business logic related to "projects". Find it in the service repository
    private final UserServiceInterface userService;                             //This service does the validating, and fetching of authenticated user info
    private final ProjectResponseMapper projectResponseMapper;

    public ProjectController(ProjectServiceInterface projectService, UserServiceInterface userService, ProjectResponseMapper projectResponseMapper) {        //We inject the services mentioned above into the class fields. Spring creates beans of these services (instances) and injects into controller (aka this class)
        this.projectService = projectService;
        this.userService = userService;
        this.projectResponseMapper = projectResponseMapper;
    }

    @Operation(summary = "Get all projects", description = "Returning a list of all projects")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Projects retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request. Invalid parameters"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - lacking authentication credentials"),
            @ApiResponse(responseCode = "403", description = "Forbidden - not permitted to access this resource"),
            @ApiResponse(responseCode = "404", description = "Project not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })

    @GetMapping("/")
    public ResponseEntity<List<ProjectResponse>> getAllProjects(@AuthenticationPrincipal Jwt jwt, @RequestParam(required = false) UUID customerId) {
        try {
            //Only managers can view all projects. Check if user has manager role

            userService.validateRequiredRoles(jwt, Role.EMPLOYEE, Role.MANAGER);

            GetProjectRequest request = new GetProjectRequest();

            if (customerId != null) {
                request.setCustomerId(customerId);
            }

            //Get all projects
            List<Project> projects = projectService.getProjects(request);

            //Return list of projects
            return ResponseEntity.ok(projects.stream().map(projectResponseMapper::mapTo).toList());
        } catch (ForbiddenException e) {   //403 - Missing permissions (i.e. user has role EMPLOYEE)
            return ResponseEntity.status(403).build();
        }  //Internal server  problem
        catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    @Operation(summary = "Get specific project based on projectId", description = "Returning a specific project using ID as a parameter")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Project retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request. Invalid parameters"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - lacking authentication credentials"),
            @ApiResponse(responseCode = "403", description = "Forbidden - not permitted to access this resource"),
            @ApiResponse(responseCode = "404", description = "Project not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })

    @GetMapping("/{projectId}")

    public ResponseEntity<ProjectResponse> getProjectById(@AuthenticationPrincipal Jwt jwt, @PathVariable UUID projectId) {
        try {
            //Validation of user role (either employee or manager)
            userService.validateRequiredRoles(jwt, Role.EMPLOYEE, Role.MANAGER);

            //Fetch project by ID

            Project project = projectService.getProject(projectId);

            //Return Project
            return ResponseEntity.ok(projectResponseMapper.mapTo(project));
        } catch (ForbiddenException e) {
            //If employee lacking permissions
            return ResponseEntity.status(403).build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(404).build();
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    @Operation(summary = "Creating new project", description = "Creating new project associated with a customer")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Project created successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request. Invalid parameters"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - lacking authentication credentials"),
            @ApiResponse(responseCode = "403", description = "Forbidden - not permitted to access this resource"),
            @ApiResponse(responseCode = "404", description = "Project not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/")
    public ResponseEntity<ProjectResponse> createProject(@RequestBody CreateProjectRequest request, @AuthenticationPrincipal Jwt jwt) {          //We need @Requestbody because we are sending JSON "payload" and Springboot will need to create a new Java object =>CreateProjectRequestDTO
        try {
            //Validation. ONLY manager can create project
            userService.validateRequiredRoles(jwt, Role.MANAGER);

            //Create project
            Project project = projectService.createProject(request);

            //Return created object and corresponding error message
            return ResponseEntity.status(201).body(projectResponseMapper.mapTo(project));
        } catch (ForbiddenException e) {
            //403 forbidden if user is employee
            return ResponseEntity.status(403).build();
        } catch (Exception e) {
            //400 Invalid parameters
            return ResponseEntity.status(400).build();
        }
    }

    //PUT api/projects/:projectId - for updating a project

    @Operation(summary = "Update project", description = "Updating the project with additional information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Project updated successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request. Invalid parameters"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - lacking authentication credentials"),
            @ApiResponse(responseCode = "403", description = "Forbidden - not permitted to access this resource"),
            @ApiResponse(responseCode = "404", description = "Project not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })

    @PutMapping("/{projectId}")

    public ResponseEntity<Void> updateProject(@AuthenticationPrincipal Jwt jwt, @PathVariable UUID projectId, @RequestBody UpdateProjectRequest request) { //void because we are not returning content
        try {
            userService.validateRequiredRoles(jwt, Role.MANAGER);
            this.projectService.updateProject(request, projectId);

            // Return 204 No Content
            return ResponseEntity.noContent().build();
        } catch (ForbiddenException e) {
            // Return 403 Forbidden if the user does not have the required role
            return ResponseEntity.status(403).build();
        } catch (EntityNotFoundException e) {
            // Return 404 Not Found if the project is not found
            return ResponseEntity.notFound().build();
        } catch (IllegalArgumentException e) {
            //400  with illegal params
            return ResponseEntity.status(400).build();
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
}


    //DELETE api/projects/:projectId - for deleting a project

    @Operation(summary = "Deleting project", description = "Removing project from the database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Project deleted successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request. Invalid parameters"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - lacking authentication credentials"),
            @ApiResponse(responseCode = "403", description = "Forbidden - not permitted to access this resource"),
            @ApiResponse(responseCode = "404", description = "Project not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })

    @DeleteMapping("/{projectId}")

    public ResponseEntity<Void> deleteProject (@AuthenticationPrincipal Jwt jwt, @PathVariable UUID projectId) {

        try{

            userService.validateRequiredRoles(jwt, Role.MANAGER);
            //Find project we need deleted
            projectService.deleteProject(projectId);

            return ResponseEntity.noContent().build();

        } catch (ForbiddenException e) {
                //user lacking permission
            return ResponseEntity.status(403).build(); //
                //project not found
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
                //server error
        }catch (Exception e) {
                return ResponseEntity.status(500).build();
        }

    }
}

