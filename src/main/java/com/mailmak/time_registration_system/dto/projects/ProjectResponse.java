package com.mailmak.time_registration_system.dto.projects;

import com.mailmak.time_registration_system.classes.Customer;
import com.mailmak.time_registration_system.classes.Project;
import com.mailmak.time_registration_system.dto.customer.CustomerResponse;
import com.mailmak.time_registration_system.dto.tasks.TaskResponse;
import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data //automatically generating getters and setters -> Simplifies boilerplate code for Java Objects
@Builder //Lombok : building a pattern for this class, enabling the construct objects
public class ProjectResponse {
    private UUID id;
    private String name;
    private String description;
    // Map to CustomerResponse (Not sure why this is necessary but customer field maps to customerResponse to show project-related info. Customer Response is not yet ready)
    private CustomerResponse customer;
}

