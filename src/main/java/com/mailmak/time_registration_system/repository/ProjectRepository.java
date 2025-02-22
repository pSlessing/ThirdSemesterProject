package com.mailmak.time_registration_system.repository;
import com.mailmak.time_registration_system.classes.Customer;
import com.mailmak.time_registration_system.classes.Project;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;
import java.util.ArrayList;

public interface ProjectRepository extends JpaRepository<Project, UUID> {             //JPARepository helps with CRUD operations and accessing the Project Entity in the database
    ArrayList<Project> findByCustomer(Customer customer);                               //Fetching all projects associated with one customer
}

