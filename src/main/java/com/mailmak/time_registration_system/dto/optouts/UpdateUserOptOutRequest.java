package com.mailmak.time_registration_system.dto.optouts;

import com.mailmak.time_registration_system.classes.Period;
import com.mailmak.time_registration_system.classes.User;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
public class UpdateUserOptOutRequest {
    private Period period;
}
