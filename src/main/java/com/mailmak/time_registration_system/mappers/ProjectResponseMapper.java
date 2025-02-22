package com.mailmak.time_registration_system.mappers;

import com.mailmak.time_registration_system.classes.Project;
import com.mailmak.time_registration_system.classes.User;
import com.mailmak.time_registration_system.dto.projects.ProjectResponse;
import com.mailmak.time_registration_system.dto.users.UserResponse;
import org.springframework.stereotype.Component;

@Component
public class ProjectResponseMapper implements ModelMapper<Project, ProjectResponse> {

    public ProjectResponseMapper() {}

    @Override
    public ProjectResponse mapTo(Project project) {
        return ProjectResponse.builder()
                .id(project.getId())
                .name(project.getName())
                .description(project.getDescription())
                .customer(new CustomerResponseMapper().mapTo(project.getCustomer()))
                .build();
    }
}
