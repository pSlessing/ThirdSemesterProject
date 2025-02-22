package com.mailmak.time_registration_system.dto.sessions;

import java.util.ArrayList;
import java.util.UUID;

import com.mailmak.time_registration_system.classes.SessionState;

import lombok.Data;

@Data
public class UpdateSessionsBatchRequest {
    private ArrayList<UUID> sessionIds;
}
