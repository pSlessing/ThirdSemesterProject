package com.mailmak.time_registration_system.dto.optouts;

import com.mailmak.time_registration_system.classes.Period;
import com.mailmak.time_registration_system.classes.User;
import lombok.Data;

@Data
public class CreateUserOptOutRequest {
    private Period period;
}
