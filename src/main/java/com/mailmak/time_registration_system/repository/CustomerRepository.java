package com.mailmak.time_registration_system.repository;

import com.mailmak.time_registration_system.classes.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface CustomerRepository extends JpaRepository<Customer, UUID> {
    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM Customer c WHERE c.id = :id")
    boolean customerExistsById(@Param("id") UUID id);

    Optional<Customer> findByName(String name);
}
