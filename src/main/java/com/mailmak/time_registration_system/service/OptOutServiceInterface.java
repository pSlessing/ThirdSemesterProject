package com.mailmak.time_registration_system.service;

import com.mailmak.time_registration_system.classes.OptOut;
import com.mailmak.time_registration_system.dto.optouts.CreateUserOptOutRequest;
import com.mailmak.time_registration_system.dto.optouts.UpdateUserOptOutRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface OptOutServiceInterface {
    boolean userHasActiveOptOut(UUID userId);
    boolean optOutStartsBefore(UUID optOutUUID, LocalDateTime endDateEntry);
    List<OptOut> getUserOptOuts(UUID userId);
    OptOut createOptOut(CreateUserOptOutRequest request, UUID userId);
    void updateOptOut(UpdateUserOptOutRequest request, UUID userId, UUID optOutId);
    void deleteOptOut(UUID outOutId, UUID userId);
}
