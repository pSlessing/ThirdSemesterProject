package com.mailmak.time_registration_system.service;

import com.mailmak.time_registration_system.classes.Project;
import com.mailmak.time_registration_system.dto.projects.CreateProjectRequest;
import com.mailmak.time_registration_system.dto.projects.GetProjectRequest;
import com.mailmak.time_registration_system.dto.projects.UpdateProjectRequest;

import java.util.ArrayList;
import java.util.UUID;

public interface ProjectServiceInterface {
    ArrayList<Project> getProjects(GetProjectRequest request);
    Project getProject(UUID id);
    Project createProject(CreateProjectRequest request);
    void updateProject(UpdateProjectRequest request, UUID projectId);
    void deleteProject(UUID project_id);
}
