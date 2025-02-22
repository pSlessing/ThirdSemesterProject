package com.mailmak.time_registration_system.dto.customer;

import lombok.Data;

import java.util.UUID;

@Data
public class UpdateCustomerRequest
{
    private UUID customerId;
    private String name;
}