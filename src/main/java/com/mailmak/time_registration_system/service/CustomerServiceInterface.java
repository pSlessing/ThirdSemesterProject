package com.mailmak.time_registration_system.service;

import com.mailmak.time_registration_system.classes.Customer;
import com.mailmak.time_registration_system.dto.customer.CreateCustomerRequest;
import com.mailmak.time_registration_system.dto.customer.UpdateCustomerRequest;

import java.util.List;
import java.util.UUID;

public interface CustomerServiceInterface {
    boolean customerExists(UUID customerId);
    List<Customer> getCustomers();
    Customer getCustomer(UUID customerId);
    Customer createCustomer(CreateCustomerRequest request);
    void updateCustomer(UpdateCustomerRequest request);
    void deleteCustomer(UUID customerId);
}