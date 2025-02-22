package com.mailmak.time_registration_system.mappers;

import com.mailmak.time_registration_system.classes.OptOut;
import com.mailmak.time_registration_system.dto.optouts.OptOutResponse;
import org.springframework.stereotype.Component;

@Component
public class OptOutResponseMapper implements ModelMapper<OptOut, OptOutResponse> {

    public OptOutResponseMapper() {}

    @Override
    public OptOutResponse mapTo(OptOut optOut) {
        return OptOutResponse.builder()
                .id(optOut.getId())
                .period(optOut.getPeriod())
                .build();
    }
}
