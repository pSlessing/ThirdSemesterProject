package com.mailmak.time_registration_system.service;

import com.mailmak.time_registration_system.classes.Customer;
import com.mailmak.time_registration_system.dto.customer.CreateCustomerRequest;
import com.mailmak.time_registration_system.dto.customer.UpdateCustomerRequest;
import com.mailmak.time_registration_system.repository.CustomerRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CustomerService implements CustomerServiceInterface {
    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    // Verifies if a customer exists by UUID
    @Override
    public boolean customerExists(UUID customerId) {
        // Delegates the check to the repository layer
        return customerRepository.customerExistsById(customerId);
    }

    // Retrieves a complete list of all customers found in the database using the findALl() function from Spring JPA
    @Override
    public List<Customer> getCustomers() {
        return customerRepository.findAll();
    }

    // Retrieves a customers name based on the ID
    @Override
    public Customer getCustomer(UUID customerId) {
        return customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
    }

    @Override
    public Customer createCustomer(CreateCustomerRequest request) {
        // Checks if a customer with the same name already exists
        Optional<Customer> existingCustomer = customerRepository.findByName(request.getName());
        if (existingCustomer.isPresent())
        {
            throw new RuntimeException("Customer with this name already exists");
        }

        // Creates a new customer object
        Customer customer = Customer.builder()
                .name(request.getName())
                .build();

        // Saves the new customer object to the database
        return customerRepository.save(customer);
    }

    @Override
    public void updateCustomer(UpdateCustomerRequest request) {
        // Finds customer based on id (UUID)
        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        // Checks if a different customer with the same name already exists
        Optional<Customer> existingCustomer = customerRepository.findByName(request.getName());
        if (existingCustomer.isPresent() && !existingCustomer.get().getId().equals(request.getCustomerId())) {
            throw new RuntimeException("Customer with this name already exists");
        }

        // Sends request to only update name in database
        customer.setName(request.getName());
        customerRepository.save(customer);
    }

    @Override
    public void deleteCustomer(UUID customerId) {
        if (!customerExists(customerId)) {
            throw new RuntimeException("Customer not found");
        }
        // Remove record of customer from database
        customerRepository.deleteById(customerId);
    }
}