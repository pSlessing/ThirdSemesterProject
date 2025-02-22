package com.mailmak.time_registration_system.dto.users;

import com.mailmak.time_registration_system.classes.Role;
import lombok.Data;

import java.util.UUID;

@Data
public class UpdateUserRequest {
    private UUID userId;
    private Role roles;
}
