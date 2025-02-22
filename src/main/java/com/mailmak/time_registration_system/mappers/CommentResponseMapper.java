package com.mailmak.time_registration_system.mappers;

import com.mailmak.time_registration_system.classes.Comment;
import com.mailmak.time_registration_system.dto.comments.CommentResponse;
import com.mailmak.time_registration_system.dto.users.UserResponse;
import org.springframework.stereotype.Component;

@Component
public class CommentResponseMapper implements ModelMapper<Comment, CommentResponse> {

    public CommentResponseMapper() {}

    @Override
    public CommentResponse mapTo(Comment comment) {
        return CommentResponse.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .createdDate(comment.getCreatedDate())
                .user(UserResponse.builder()
                        .id(comment.getUser().getId())
                        .name(comment.getUser().getName())
                        .email(comment.getUser().getEmail())
                        .roles(comment.getUser().getRoles())
                        .build())
                .build();
    }
}
