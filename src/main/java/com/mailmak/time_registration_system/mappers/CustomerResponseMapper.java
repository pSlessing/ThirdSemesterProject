package com.mailmak.time_registration_system.mappers;

import com.mailmak.time_registration_system.classes.Customer;
import com.mailmak.time_registration_system.classes.User;
import com.mailmak.time_registration_system.dto.customer.CustomerResponse;
import com.mailmak.time_registration_system.dto.users.UserResponse;
import org.springframework.stereotype.Component;

@Component
public class CustomerResponseMapper implements ModelMapper<Customer, CustomerResponse> {

    public CustomerResponseMapper() {}

    @Override
    public CustomerResponse mapTo(Customer customer) {
        return CustomerResponse.builder()
                .id(customer.getId())
                .name(customer.getName())
                .build();
    }
}
