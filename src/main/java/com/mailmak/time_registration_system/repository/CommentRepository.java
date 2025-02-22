package com.mailmak.time_registration_system.repository;

import com.mailmak.time_registration_system.classes.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CommentRepository extends JpaRepository<Comment, UUID> {
    List<Comment> findByTaskId(UUID taskId);
}
