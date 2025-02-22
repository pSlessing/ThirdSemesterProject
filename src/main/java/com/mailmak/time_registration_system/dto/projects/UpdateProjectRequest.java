package com.mailmak.time_registration_system.dto.projects;
import lombok.Data;

import java.util.UUID;

@Data
public class UpdateProjectRequest {
    private String name;
    private String description;
    private UUID customerId;
}
