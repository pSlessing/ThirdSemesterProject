package com.mailmak.time_registration_system.repository;

import com.mailmak.time_registration_system.classes.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByPrincipalID(UUID principalId);
}
