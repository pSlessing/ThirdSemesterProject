package com.mailmak.time_registration_system.service;

import com.mailmak.time_registration_system.classes.Comment;
import com.mailmak.time_registration_system.classes.Task;
import com.mailmak.time_registration_system.classes.User;
import com.mailmak.time_registration_system.dto.comments.CommentResponse;
import com.mailmak.time_registration_system.dto.comments.CreateCommentRequest;
import com.mailmak.time_registration_system.dto.comments.UpdateCommentRequest;
import com.mailmak.time_registration_system.dto.users.UserResponse;
import com.mailmak.time_registration_system.repository.CommentRepository;
import com.mailmak.time_registration_system.repository.TaskRepository;
import com.mailmak.time_registration_system.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class CommentService implements CommentServiceInterface {

    private final CommentRepository commentRepository;
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    @Autowired
    public CommentService(CommentRepository commentRepository, TaskRepository taskRepository, UserRepository userRepository) {
        this.commentRepository = commentRepository;
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }

    public Comment createComment(CreateCommentRequest request) {
        Task task = taskRepository.findById(request.getTaskId())
                .orElseThrow(() -> new EntityNotFoundException("Task with ID " + request.getTaskId() + " not found"));

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("User with ID " + request.getUserId() + " not found"));

        Comment comment = Comment.builder()
                .content(request.getContent())
                .createdDate(LocalDateTime.now())
                .task(task)
                .user(user)
                .build();

        commentRepository.save(comment);

        return comment;
    }

    @Override
    public List<Comment> getComments(UUID taskId) {
        return commentRepository.findByTaskId(taskId);
    }

    @Override
    public void updateComment(UpdateCommentRequest request) {
        Comment comment = commentRepository.findById(request.getCommentId())
                .orElseThrow(() -> new EntityNotFoundException("Comment with ID " + request.getCommentId() + " not found"));

        comment.setContent(request.getContent());
        commentRepository.save(comment);
    }

    @Override
    public void deleteComment(UUID commentId) {
        if (!commentRepository.existsById(commentId)) {
            throw new EntityNotFoundException("Comment with ID " + commentId + " not found");
        }

        commentRepository.deleteById(commentId);
    }
}
