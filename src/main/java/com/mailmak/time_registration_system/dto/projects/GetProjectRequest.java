package com.mailmak.time_registration_system.dto.projects;

import java.util.UUID;
import lombok.Data;

@Data
public class GetProjectRequest {
    public UUID customerId;
}
