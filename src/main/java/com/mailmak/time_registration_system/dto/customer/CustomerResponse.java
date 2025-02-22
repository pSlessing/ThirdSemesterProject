package com.mailmak.time_registration_system.dto.customer;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class CustomerResponse
{
    private UUID id;
    private String name;
}