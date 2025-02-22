package com.mailmak.time_registration_system.repository;

import com.mailmak.time_registration_system.classes.OptOut;
import com.mailmak.time_registration_system.classes.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OptOutRepository extends JpaRepository<OptOut, UUID> {
    List<OptOut> findByUser(User user);
}
