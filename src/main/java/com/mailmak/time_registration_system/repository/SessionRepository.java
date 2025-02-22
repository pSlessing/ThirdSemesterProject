package com.mailmak.time_registration_system.repository;

import com.mailmak.time_registration_system.classes.ProjectSession;

import com.mailmak.time_registration_system.classes.Session;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

public interface SessionRepository extends JpaRepository<ProjectSession, UUID> {
    ArrayList<ProjectSession> findAll(Specification<ProjectSession> specification);
}
