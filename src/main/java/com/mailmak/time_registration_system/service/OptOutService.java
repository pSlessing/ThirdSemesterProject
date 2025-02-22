package com.mailmak.time_registration_system.service;

import com.mailmak.time_registration_system.classes.OptOut;
import com.mailmak.time_registration_system.classes.Period;
import com.mailmak.time_registration_system.classes.User;
import com.mailmak.time_registration_system.dto.optouts.CreateUserOptOutRequest;
import com.mailmak.time_registration_system.dto.optouts.OptOutResponse;
import com.mailmak.time_registration_system.dto.optouts.UpdateUserOptOutRequest;
import com.mailmak.time_registration_system.exceptions.ForbiddenException;
import com.mailmak.time_registration_system.repository.OptOutRepository;
import com.mailmak.time_registration_system.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class OptOutService implements OptOutServiceInterface {

    private final OptOutRepository optOutRepository;
    private final UserRepository userRepository;

    @Autowired
    public OptOutService(OptOutRepository optOutRepository, UserRepository userRepository)
    {
        this.optOutRepository = optOutRepository;
        this.userRepository = userRepository;
    }

    @Override
    public boolean userHasActiveOptOut(UUID userId)
    {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + userId));

        LocalDateTime now = LocalDateTime.now();

        // Check if any of the user's opt-outs are active -
        return user.getOptOuts().stream()
                .anyMatch(optOut -> {
                    Period period = optOut.getPeriod();
                    return  (period.getEndDate() == null || (now.isEqual(period.getStartDate()) || now.isAfter(period.getStartDate())) &&
                            (period.getEndDate() == null || now.isBefore(period.getEndDate()) || now.isEqual(period.getEndDate())));
                });
    }

    @Override
    public boolean optOutStartsBefore(UUID optOutUUID, LocalDateTime endDateEntry)
    {
        OptOut checkOptOut = optOutRepository.findById(optOutUUID)
                .orElseThrow(() -> new EntityNotFoundException("OptOut not found with ID: " + optOutUUID));

        //StartDate is not nullable, checks if the start date is before the enddate
        return checkOptOut.getPeriod().getStartDate().isBefore(endDateEntry);
    }

    @Override
    public List<OptOut> getUserOptOuts(UUID userId)
    {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + userId));


        return optOutRepository.findByUser(user);
    }

    @Override
    public OptOut createOptOut(CreateUserOptOutRequest request, UUID userId)
    {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + userId));

        Period period = request.getPeriod();

        if (period.getStartDate() == null)
        {
            period.setStartDate(LocalDateTime.now());
        }

        if (period.getEndDate() != null && period.getStartDate().isAfter(period.getEndDate()))
        {
            throw new IllegalArgumentException("Start date cannot be after end date");
        }

        OptOut optOut = OptOut.builder()
                .period(request.getPeriod())
                .user(user)
                .build();

        return this.optOutRepository.save(optOut);
    }

    @Override
    public void updateOptOut(UpdateUserOptOutRequest request, UUID userId, UUID optOutId)
    {
        OptOut optOut = optOutRepository.findById(optOutId)
                .orElseThrow(() -> new EntityNotFoundException("OptOut not found with ID: " + optOutId));

        if(!optOut.getUser().getId().equals(userId)){
           throw new ForbiddenException("User does not belong to this optOut");
        }

        optOut.setPeriod(request.getPeriod());
        optOutRepository.save(optOut);
    }

    @Override
    public void deleteOptOut(UUID outOutId, UUID userId)
    {
        OptOut optOut = optOutRepository.findById(outOutId)
                .orElseThrow(() -> new EntityNotFoundException("OptOut not found with ID: " + outOutId));

        if(!optOut.getUser().getId().equals(userId)){
            throw new ForbiddenException("User does not belong to this optOut");
        }

        optOutRepository.delete(optOut);
    }
}
