package com.mailmak.time_registration_system.dto.optouts;

import com.mailmak.time_registration_system.classes.Period;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class OptOutResponse {
    UUID id;
    Period period;
}
