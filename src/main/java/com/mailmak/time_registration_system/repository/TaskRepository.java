package com.mailmak.time_registration_system.repository;

import com.mailmak.time_registration_system.classes.Task;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

public interface TaskRepository extends JpaRepository<Task, UUID> {
    @EntityGraph(attributePaths = {"project"})
    ArrayList<Task> findByProjectId(UUID projectId);
}
