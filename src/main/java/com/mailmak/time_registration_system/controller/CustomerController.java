package com.mailmak.time_registration_system.controller;

import com.mailmak.time_registration_system.classes.Customer;
import com.mailmak.time_registration_system.classes.Role;
import com.mailmak.time_registration_system.dto.customer.CreateCustomerRequest;
import com.mailmak.time_registration_system.dto.customer.CustomerResponse;
import com.mailmak.time_registration_system.dto.customer.UpdateCustomerRequest;
import com.mailmak.time_registration_system.exceptions.ForbiddenException;
import com.mailmak.time_registration_system.mappers.CustomerResponseMapper;
import com.mailmak.time_registration_system.service.CustomerServiceInterface;
import com.mailmak.time_registration_system.service.UserServiceInterface;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/customers")
@Tag(name = "Customers", description = "Endpoints for managing customers")
public class CustomerController {
    private final CustomerServiceInterface customerService;
    private final UserServiceInterface userService;
    private final CustomerResponseMapper customerResponseMapper;

    public CustomerController(CustomerServiceInterface customerService, UserServiceInterface userService, CustomerResponseMapper customerResponseMapper) {
        this.customerService = customerService;
        this.userService = userService;
        this.customerResponseMapper = customerResponseMapper;
    }

    /*

    These are validation methods not needed as endpoints

    @PreAuthorize cannot be used here as it checks if the token has the required role.
    The roles are not stored on the token, but on the user objet in the database.

    @Operation(
            summary = "Check if customer exists by ID",
            description = "Verifies if a customer with the given UUID exists in the system"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Response with existence status"),
            @ApiResponse(responseCode = "400", description = "Bad request - invalid input parameters"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - not logged in"),
            @ApiResponse(responseCode = "403", description = "Forbidden - insufficient permissions"),
            @ApiResponse(responseCode = "404", description = "Customer not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/exists/{customerId}")
    @PreAuthorize("hasRole('EMPLOYEE') || hasRole('MANAGER')")
    public ResponseEntity<Boolean> checkCustomerExistsById(@PathVariable UUID customerId) {
        boolean exists = customerService.CustomerExists(customerId);
        return ResponseEntity.ok(exists);
    }

    @Operation(
            summary = "Check if customer exists by name",
            description = "Verifies if a customer exists by name"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Response with existence status"),
            @ApiResponse(responseCode = "400", description = "Bad request - invalid input parameters"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - not logged in"),
            @ApiResponse(responseCode = "403", description = "Forbidden - insufficient permissions"),
            @ApiResponse(responseCode = "404", description = "Customer not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/exists/{name}")
    @PreAuthorize("hasRole('EMPLOYEE') || hasRole('MANAGER')")
    public ResponseEntity<Boolean> checkCustomerExistsByName(@PathVariable String name) {
        boolean exists = customerService.CustomerExists(name);
        return ResponseEntity.ok(exists);
    }

    @Operation(
            summary = "Check if customer exists by name and ID",
            description = "Verifies if a customer with the given name and ID exists in the system"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Response with existence status"),
            @ApiResponse(responseCode = "400", description = "Bad request - invalid input parameters"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - not logged in"),
            @ApiResponse(responseCode = "403", description = "Forbidden - insufficient permissions"),
            @ApiResponse(responseCode = "404", description = "Customer not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/name/{name}/id/{customerId}")
    @PreAuthorize("hasRole('EMPLOYEE') || hasRole('MANAGER')")
    public ResponseEntity<Boolean> checkCustomerExistsByNameAndId(
            @PathVariable String name,
            @PathVariable UUID customerId) {
        boolean exists = customerService.CustomerExists(name, customerId);
        return ResponseEntity.ok(exists);
    }
    */

    @Operation(
            summary = "Get all customers",
            description = "Retrieves a list of all customers in the system"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved customer list"),
            @ApiResponse(responseCode = "400", description = "Bad request - invalid input parameters"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - not logged in"),
            @ApiResponse(responseCode = "403", description = "Forbidden - insufficient permissions"),
            @ApiResponse(responseCode = "404", description = "No customers found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/")
    public ResponseEntity<List<CustomerResponse>> getCustomers(@AuthenticationPrincipal Jwt jwt) {
        try {
            userService.validateRequiredRoles(jwt, Role.EMPLOYEE, Role.MANAGER);

            List<Customer> customers = customerService.getCustomers();
            return ResponseEntity.ok(customers.stream().map(customerResponseMapper::mapTo).toList());
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
        catch (ForbiddenException e)
        {
            return ResponseEntity.status(403).build();
        }
        catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    @Operation(
            summary = "Create a customer",
            description = "Creates a new customer in the system"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successfully created customer"),
            @ApiResponse(responseCode = "400", description = "Bad request - invalid input parameters"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - not logged in"),
            @ApiResponse(responseCode = "403", description = "Forbidden - insufficient permissions"),
            @ApiResponse(responseCode = "404", description = "No customers found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/")
    public ResponseEntity<CustomerResponse> createCustomers(@RequestBody CreateCustomerRequest request, @AuthenticationPrincipal Jwt jwt) {
        try {
            userService.validateRequiredRoles(jwt, Role.MANAGER);

            Customer customer = customerService.createCustomer(request);
            return ResponseEntity.status(201).body(customerResponseMapper.mapTo(customer));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
        catch (ForbiddenException e)
        {
            return ResponseEntity.status(403).build();
        }
        catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    @Operation(
            summary = "Get customer by ID",
            description = "Retrieves detailed information about a specific customer"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved customer details"),
            @ApiResponse(responseCode = "400", description = "Bad request - invalid input parameters"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - not logged in"),
            @ApiResponse(responseCode = "403", description = "Forbidden - insufficient permissions"),
            @ApiResponse(responseCode = "404", description = "Customer not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/{customerId}")
    public ResponseEntity<CustomerResponse> getCustomer(@PathVariable UUID customerId, @AuthenticationPrincipal Jwt jwt) {
        try {
            userService.validateRequiredRoles(jwt, Role.EMPLOYEE, Role.MANAGER);

            Customer customer = customerService.getCustomer(customerId);
            return ResponseEntity.ok(customerResponseMapper.mapTo(customer));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
        catch (ForbiddenException e)
        {
            return ResponseEntity.status(403).build();
        }
        catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    @Operation(
            summary = "Update customer",
            description = "Updates the information of an existing customer. Requires MANAGER role."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Customer successfully updated"),
            @ApiResponse(responseCode = "400", description = "Bad request - invalid input parameters"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - not logged in"),
            @ApiResponse(responseCode = "403", description = "Forbidden - insufficient permissions"),
            @ApiResponse(responseCode = "404", description = "Customer not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping("/{customerId}")
    public ResponseEntity<Void> updateCustomer(@PathVariable UUID customerId, @RequestBody UpdateCustomerRequest request, @AuthenticationPrincipal Jwt jwt) {
        try {
            userService.validateRequiredRoles(jwt, Role.MANAGER, Role.MANAGER);

            request.setCustomerId(customerId);
            customerService.updateCustomer(request);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
        catch (ForbiddenException e)
        {
            return ResponseEntity.status(403).build();
        }
        catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    @Operation(
            summary = "Delete customer",
            description = "Deletes a customer from the system"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Customer successfully deleted"),
            @ApiResponse(responseCode = "400", description = "Bad request - invalid input parameters"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - not logged in"),
            @ApiResponse(responseCode = "403", description = "Forbidden - insufficient permissions"),
            @ApiResponse(responseCode = "404", description = "Customer not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @DeleteMapping("/{customerId}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable UUID customerId, @AuthenticationPrincipal Jwt jwt) {
        try {
            userService.validateRequiredRoles(jwt, Role.MANAGER);

            customerService.deleteCustomer(customerId);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
        catch (ForbiddenException e)
        {
            return ResponseEntity.status(403).build();
        }
        catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }
}